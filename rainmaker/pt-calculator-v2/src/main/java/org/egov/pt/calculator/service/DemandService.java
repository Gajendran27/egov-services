package org.egov.pt.calculator.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.calculator.repository.Repository;
import org.egov.pt.calculator.util.CalculatorConstants;
import org.egov.pt.calculator.util.CalculatorUtils;
import org.egov.pt.calculator.util.Configurations;
import org.egov.pt.calculator.web.models.Assessment;
import org.egov.pt.calculator.web.models.Calculation;
import org.egov.pt.calculator.web.models.CalculationCriteria;
import org.egov.pt.calculator.web.models.CalculationReq;
import org.egov.pt.calculator.web.models.GetBillCriteria;
import org.egov.pt.calculator.web.models.TaxHeadEstimate;
import org.egov.pt.calculator.web.models.demand.BillResponse;
import org.egov.pt.calculator.web.models.demand.Demand;
import org.egov.pt.calculator.web.models.demand.DemandDetail;
import org.egov.pt.calculator.web.models.demand.DemandRequest;
import org.egov.pt.calculator.web.models.demand.DemandResponse;
import org.egov.pt.calculator.web.models.demand.DemandStatus;
import org.egov.pt.calculator.web.models.demand.TaxHeadMaster;
import org.egov.pt.calculator.web.models.property.OwnerInfo;
import org.egov.pt.calculator.web.models.property.Property;
import org.egov.pt.calculator.web.models.property.PropertyDetail;
import org.egov.pt.calculator.web.models.property.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.egov.tracer.model.ServiceCallException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

@Service
@Slf4j
public class DemandService {

	@Autowired
	private EstimationService estimationService;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Configurations configs;

	@Autowired
	private AssessmentService assessmentService;

	@Autowired
	private CalculatorUtils utils;

	@Autowired
	private Repository repository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PayService payService;

	@Autowired
	private MasterDataService mstrDataService;

	/**
	 * Generates and persists the demand to billing service for the given property
	 * 
	 * if the property has been assessed already for the given financial year then
	 * 
	 * it carry forwards the old collection amount to the new demand as advance
	 * 
	 * @param request
	 * @return
	 */
	public Map<String, Calculation> generateDemands(CalculationReq request) {

		List<CalculationCriteria> criterias = request.getCalculationCriteria();
		List<Demand> demands = new ArrayList<>();
		List<String> lesserAssessments = new ArrayList<>();
		
		Map<String, Calculation> propertyCalculationMap = estimationService.getEstimationPropertyMap(request);
		for (CalculationCriteria criteria : criterias) {

			String assessmentNumber = criteria.getProperty().getPropertyDetails().get(0).getAssessmentNumber();
			BigDecimal newTax = propertyCalculationMap.get(assessmentNumber).getTaxAmount();
			
			BigDecimal carryForwardCollectedAmount = getCarryForwardAndCancelOldDemand(newTax, criteria,
					request.getRequestInfo());

			if (carryForwardCollectedAmount.doubleValue() >= 0.0) {
				Property property = criteria.getProperty();

				Demand demand = prepareDemand(property,
						propertyCalculationMap.get(property.getPropertyDetails().get(0).getAssessmentNumber()));

				if (carryForwardCollectedAmount.doubleValue() > 0.0)
					demand.getDemandDetails()
							.add(DemandDetail.builder().taxAmount(carryForwardCollectedAmount.negate())
									.tenantId(criteria.getTenantId()).demandId(demand.getId())
									.taxHeadMasterCode(CalculatorConstants.PT_ADVANCE_CARRYFORWARD).build());
				demands.add(demand);
			}else {
				lesserAssessments.add(assessmentNumber);
			}
		}
		
		if (!CollectionUtils.isEmpty(lesserAssessments)) {
			throw new CustomException("EG_PT_DEPRECIATING_ASSESSMENT_ERROR",
					"Depreciating assessments are not allowed, please kindly update the values for the following properties : "
							+ lesserAssessments);
		}
		
		DemandRequest dmReq = DemandRequest.builder().demands(demands).requestInfo(request.getRequestInfo()).build();
		String url = new StringBuilder().append(configs.getBillingServiceHost())
				.append(configs.getDemandCreateEndPoint()).toString();
		DemandResponse res = new DemandResponse();

		try {
			res = restTemplate.postForObject(url, dmReq, DemandResponse.class);
		} catch (HttpClientErrorException e) {
			throw new ServiceCallException(e.getResponseBodyAsString());
		}
		log.info(" The demand Response is : " + res);
		assessmentService.saveAssessments(res.getDemands(), request.getRequestInfo());
		return propertyCalculationMap;
	}

	/**
	 * Generates and returns bill from billing service
	 * 
	 * updates the demand with penalty and rebate if applicable before generating
	 * bill
	 * 
	 * NOTE : This method updates the master data map in masterDataService
	 * 
	 * @param getBillCriteria
	 * @param requestInfo
	 * @return
	 */
	public BillResponse getBill(GetBillCriteria getBillCriteria, RequestInfoWrapper requestInfoWrapper) {

		RequestInfo requestInfo = requestInfoWrapper.getRequestInfo();
		Map<String, Map<String, List<Object>>> propertyBasedExemptionMasterMap = new HashMap<>();
		Map<String, JSONArray> timeBasedExmeptionMasterMap = new HashMap<>();
		mstrDataService.setPropertyMasterValues(requestInfo, getBillCriteria.getTenantId(),
				propertyBasedExemptionMasterMap, timeBasedExmeptionMasterMap);

		DemandResponse res = mapper.convertValue(
				repository.fetchResult(utils.getDemandSearchUrl(getBillCriteria), requestInfoWrapper),
				DemandResponse.class);
		if (CollectionUtils.isEmpty(res.getDemands())) {
			Map<String, String> map = new HashMap<>();
			map.put(CalculatorConstants.EMPTY_DEMAND_ERROR_CODE, CalculatorConstants.EMPTY_DEMAND_ERROR_MESSAGE);
			throw new CustomException(map);
		}
		Demand demand = res.getDemands().get(0);
		applytimeBasedApplicables(demand, getBillCriteria.getAssessmentYear(), timeBasedExmeptionMasterMap);
		DemandRequest request = DemandRequest.builder().demands(Arrays.asList(demand)).requestInfo(requestInfo).build();
		StringBuilder updateDemandUrl = utils.getUpdateDemandUrl();
		repository.fetchResult(updateDemandUrl, request);
		StringBuilder billGenUrl = utils.getBillGenUrl(getBillCriteria.getTenantId(), demand.getId(), demand.getConsumerCode());
		return mapper.convertValue(repository.fetchResult(billGenUrl, requestInfoWrapper), BillResponse.class);
	}

	/**
	 * if any previous assessments and demands associated with it exists for the
	 * same financial year
	 * 
	 * Then Returns the collected amount of previous demand if the current
	 * assessment is for the current year
	 * 
	 * and cancels the previous demand by updating it's status to inactive
	 * 
	 * @param criteria
	 * @return
	 */
	private BigDecimal getCarryForwardAndCancelOldDemand(BigDecimal newTax, CalculationCriteria criteria, RequestInfo requestInfo) {

		Property property = criteria.getProperty();

		BigDecimal carryForward = BigDecimal.ZERO;
		BigDecimal oldTaxAmt = BigDecimal.ZERO;

		Assessment assessment = Assessment.builder().propertyId(property.getPropertyId())
				.tenantId(property.getTenantId())
				.assessmentYear(property.getPropertyDetails().get(0).getFinancialYear()).build();

		List<Assessment> assessments = assessmentService.getMaxAssessment(assessment);

		if (CollectionUtils.isEmpty(assessments)) return carryForward;

		Assessment latestAssessment = assessments.get(0);
		log.debug(" the lates assessment : " + latestAssessment);

		DemandResponse res = mapper.convertValue(
				repository.fetchResult(utils.getDemandSearchUrl(latestAssessment), new RequestInfoWrapper(requestInfo)),
				DemandResponse.class);
		Demand demand = res.getDemands().get(0);

		Map<String, Boolean> isTaxHeadDebitMap = mstrDataService
				.getTaxHeadMasterMap(requestInfo, property.getTenantId()).stream()
				.collect(Collectors.toMap(TaxHeadMaster::getCode, TaxHeadMaster::getIsDebit));

		for (DemandDetail detail : demand.getDemandDetails())
			if (!isTaxHeadDebitMap.get(detail.getTaxHeadMasterCode()))
				oldTaxAmt = oldTaxAmt.add(detail.getTaxAmount());

		carryForward = utils.getTotalCollectedAmountAndSetTaxAmt(demand, isTaxHeadDebitMap);

		if (oldTaxAmt.compareTo(newTax) > 0)
			carryForward = BigDecimal.valueOf(-1);

		if (BigDecimal.ZERO.compareTo(carryForward) > 0) return carryForward;
		
		demand.setStatus(DemandStatus.CANCELLED);
		DemandRequest request = DemandRequest.builder().demands(Arrays.asList(demand)).requestInfo(requestInfo).build();
		StringBuilder updateDemandUrl = utils.getUpdateDemandUrl();
		repository.fetchResult(updateDemandUrl, request);

		return carryForward;
	}

	/**
	 * Prepares Demand object based on the incoming calculation object and property
	 * 
	 * @param property
	 * @param calculation
	 * @return
	 */
	private Demand prepareDemand(Property property, Calculation calculation) {

		String tenantId = property.getTenantId();
		PropertyDetail detail = property.getPropertyDetails().get(0);
		String propertyType = detail.getPropertyType();
		String consumerCode = property.getPropertyId() + CalculatorConstants.PT_CONSUMER_CODE_SEPARATOR + detail.getAssessmentNumber();
		OwnerInfo owner = null;
		if (null != detail.getCitizenInfo())
			owner = detail.getCitizenInfo();
		else
			owner = detail.getOwners().iterator().next();

		List<DemandDetail> details = new ArrayList<>();

		for (TaxHeadEstimate estimate : calculation.getTaxHeadEstimates())
				details.add(DemandDetail.builder().taxHeadMasterCode(estimate.getTaxHeadCode())
						.taxAmount(estimate.getEstimateAmount()).tenantId(tenantId).build());

		return Demand.builder().tenantId(tenantId).businessService(configs.getPtModuleCode()).consumerType(propertyType)
				.consumerCode(consumerCode).owner(owner).taxPeriodFrom(calculation.getFromDate())
				.taxPeriodTo(calculation.getToDate())
				.minimumAmountPayable(BigDecimal.valueOf(configs.getPtMinAmountPayable())).demandDetails(details)
				.build();
	}

	/**
	 * Applies Penalty/Rebate/Interest to the incoming demands
	 * 
	 * If applied already then the demand details will be updated
	 * 
	 * @param demand
	 * @param assessmentYear
	 * @return
	 */
	private void applytimeBasedApplicables(Demand demand, String assessmentYear,
			Map<String, JSONArray> timeBasedExmeptionMasterMap) {

		String tenantId = demand.getTenantId();
		String demandId = demand.getId();
		Long lastCollectedTime = 0l;
		
		BigDecimal taxAmt = utils.getTaxAmtFromDemandForApplicablesGeneration(demand);
		BigDecimal collectedAmount = BigDecimal.ZERO;
		BigDecimal oldInterest = BigDecimal.ZERO;
		BigDecimal decimalCredit = BigDecimal.ZERO;
		BigDecimal decimalDebit = BigDecimal.ZERO;
		
		for (DemandDetail detail : demand.getDemandDetails()) {
			collectedAmount = collectedAmount.add(detail.getCollectionAmount());
			if (detail.getTaxHeadMasterCode().equalsIgnoreCase(CalculatorConstants.PT_TIME_INTEREST)) {
				oldInterest = detail.getTaxAmount();
				if (detail.getCollectionAmount().doubleValue() > 0.0)
					lastCollectedTime = detail.getAuditDetail().getLastModifiedTime();
			}
			if(detail.getTaxHeadMasterCode().equalsIgnoreCase(CalculatorConstants.PT_DECIMAL_CEILING_CREDIT))
				decimalCredit = detail.getTaxAmount();
			else if(detail.getTaxHeadMasterCode().equalsIgnoreCase(CalculatorConstants.PT_DECIMAL_CEILING_DEBIT))
				decimalDebit = detail.getTaxAmount();
		}

		boolean isRebateUpdated = false;
		boolean isPenaltyUpdated = false;
		boolean isInterestUpdated = false;
		boolean isDecimalMathcing = false;
		
		List<DemandDetail> details = demand.getDemandDetails();
		Map<String, BigDecimal> rebatePenaltyEstimates = payService.applyPenaltyRebateAndInterest(taxAmt,
				collectedAmount, lastCollectedTime, assessmentYear, timeBasedExmeptionMasterMap);
		
		if(null == rebatePenaltyEstimates) return;
		
		BigDecimal rebate = rebatePenaltyEstimates.get(CalculatorConstants.PT_TIME_REBATE);
		BigDecimal penalty = rebatePenaltyEstimates.get(CalculatorConstants.PT_TIME_PENALTY);
		BigDecimal interest = rebatePenaltyEstimates.get(CalculatorConstants.PT_TIME_INTEREST);
		
		if (lastCollectedTime > 0)
			interest = oldInterest.add(rebatePenaltyEstimates.get(CalculatorConstants.PT_TIME_INTEREST));
		
		TaxHeadEstimate estimate = payService.roundOfDecimals(taxAmt.add(penalty).add(interest).add(decimalCredit),
				rebate.add(decimalDebit));

		for (DemandDetail detail : details) {
			if (detail.getTaxHeadMasterCode().equalsIgnoreCase(CalculatorConstants.PT_TIME_REBATE)) {
				detail.setTaxAmount(rebate);
				isRebateUpdated = true;
			}
			if (detail.getTaxHeadMasterCode().equalsIgnoreCase(CalculatorConstants.PT_TIME_PENALTY)) {
				detail.setTaxAmount(penalty);
				isPenaltyUpdated = true;
			}
			if (detail.getTaxHeadMasterCode().equalsIgnoreCase(CalculatorConstants.PT_TIME_INTEREST)) {
				detail.setTaxAmount(interest);
				isInterestUpdated = true;
			}
			
			if (detail.getTaxHeadMasterCode().equalsIgnoreCase(CalculatorConstants.PT_DECIMAL_CEILING_CREDIT)
					&& decimalCredit.doubleValue() > 0.0) {
				detail.setTaxAmount(decimalCredit);
				isDecimalMathcing = true;
			}

			if (detail.getTaxHeadMasterCode().equalsIgnoreCase(CalculatorConstants.PT_DECIMAL_CEILING_DEBIT)
					&& decimalDebit.doubleValue() > 0.0) {
				detail.setTaxAmount(decimalDebit);
				isDecimalMathcing = true;
			}
		}
		if (!isPenaltyUpdated && penalty.compareTo(BigDecimal.ZERO) > 0)
			details.add(DemandDetail.builder().taxAmount(penalty).taxHeadMasterCode(CalculatorConstants.PT_TIME_PENALTY)
					.demandId(demandId).tenantId(tenantId).build());
		if (!isRebateUpdated && rebate.compareTo(BigDecimal.ZERO) > 0)
			details.add(DemandDetail.builder().taxAmount(rebate)
					.taxHeadMasterCode(CalculatorConstants.PT_TIME_REBATE).demandId(demandId).tenantId(tenantId)
					.build());
		if (!isInterestUpdated && interest.compareTo(BigDecimal.ZERO) > 0)
			details.add(
					DemandDetail.builder().taxAmount(interest).taxHeadMasterCode(CalculatorConstants.PT_TIME_INTEREST)
							.demandId(demandId).tenantId(tenantId).build());

		if (!isDecimalMathcing && null != estimate && BigDecimal.ZERO.compareTo(estimate.getEstimateAmount()) <= 0)
			details.add(DemandDetail.builder().taxAmount(estimate.getEstimateAmount())
					.taxHeadMasterCode(estimate.getTaxHeadCode()).demandId(demandId).tenantId(tenantId).build());
	}
}
