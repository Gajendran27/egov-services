package org.egov.pt.calculator.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.egov.pt.calculator.config.CalculatorConfig;
import org.egov.pt.calculator.web.models.CalculationCriteria;
import org.egov.pt.calculator.web.models.CalculationReq;
import org.egov.pt.calculator.web.models.demand.Demand;
import org.egov.pt.calculator.web.models.demand.DemandDetail;
import org.egov.pt.calculator.web.models.demand.DemandRequest;
import org.egov.pt.calculator.web.models.property.Property;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DemandService {

	@Autowired
	private CalculatorService calculator;

	public void generateDemands(CalculationReq request) {

		List<CalculationCriteria> props = request.getCalculationCriteria();
		List<Demand> demands = new ArrayList<>();

		props.forEach(criteria -> {

			Map<String, Double> taxHeadTaxAmtMap = calculator.getCalculatedMap(criteria);
			demands.add(getDemand(criteria, taxHeadTaxAmtMap));
		});

		DemandRequest.builder().demands(demands).requestInfo(request.getRequestInfo()).build();
		/*
		 * post demand Request to billing service
		 */
	}

	private Demand getDemand(CalculationCriteria criteria, Map<String, Double> taxHeadTaxAmtMap) {

		Property property = criteria.getProperty();
		String tenantId = property.getTenantId();
		String propertyType = property.getPropertyType();
		List<DemandDetail> details = new ArrayList<>();

		for (Entry<String, Double> entry : taxHeadTaxAmtMap.entrySet())
			details.add(DemandDetail.builder().taxHeadMasterCode(entry.getKey())
					.taxAmount(BigDecimal.valueOf(entry.getValue())).tenantId(tenantId).build());

		/*
		 * change  owner info logic to a better one considering all the scenarios
		 * 
		 * get financial year object to get the from and to date 
		 */
		return Demand.builder().tenantId(tenantId).businessService(CalculatorConfig.SERVICE_NAME)
				.consumerType(propertyType).consumerCode(property.getAssessmentNumber()).owner(property.getOwners().get(0))
				.minimumAmountPayable(CalculatorConfig.MIN_AMT_PAYABLE).demandDetails(details).build();
	}
}
