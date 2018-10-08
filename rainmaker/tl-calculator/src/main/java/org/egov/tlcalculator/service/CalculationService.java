package org.egov.tlcalculator.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tlcalculator.config.TLCalculatorConfigs;
import org.egov.tlcalculator.repository.BillingslabQueryBuilder;
import org.egov.tlcalculator.repository.BillingslabRepository;
import org.egov.tlcalculator.repository.ServiceRequestRepository;
import org.egov.tlcalculator.utils.CalculationUtils;
import org.egov.tlcalculator.web.models.*;
import org.egov.tlcalculator.web.models.tradelicense.TradeLicense;
import org.egov.tlcalculator.web.models.demand.Category;
import org.egov.tlcalculator.web.models.demand.TaxHeadEstimate;
import org.egov.tlcalculator.web.models.tradelicense.TradeUnit;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


@Service
@Slf4j
public class CalculationService {


    @Autowired
    private BillingslabRepository repository;

    @Autowired
    private BillingslabQueryBuilder queryBuilder;

    @Autowired
    private TLCalculatorConfigs config;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private CalculationUtils utils;



  public List<Calculation> calculate(RequestInfo requestInfo,List<CalulationCriteria> criterias){
      List<Calculation> calculations = new LinkedList<>();
      for(CalulationCriteria criteria : criterias) {
          TradeLicense license;
          if (criteria.getTradelicense()==null && criteria.getApplicationNumber() != null) {
              license = utils.getTradeLicense(requestInfo, criteria.getApplicationNumber(), criteria.getTenantId());
              criteria.setTradelicense(license);
          }
          Calculation calculation = new Calculation();
          calculation.setTradeLicense(criteria.getTradelicense());
          calculation.setTenantId(criteria.getTenantId());
          calculation.setTaxHeadEstimates(getTaxHeadEstimates(criteria));
          calculations.add(calculation);
      }
      return calculations;
  }



    private List<TaxHeadEstimate> getTaxHeadEstimates(CalulationCriteria calulationCriteria){
      List<TaxHeadEstimate> estimates = new LinkedList<>();

      estimates.add(getBaseTax(calulationCriteria));

      if(calulationCriteria.getTradelicense().getTradeLicenseDetail().getAdhocPenalty()!=null)
          estimates.add(getAdhocPenalty(calulationCriteria));

      if(calulationCriteria.getTradelicense().getTradeLicenseDetail().getAdhocExemption()!=null)
          estimates.add(getAdhocExemption(calulationCriteria));

      return estimates;
  }



  private TaxHeadEstimate getBaseTax(CalulationCriteria calulationCriteria){
      TradeLicense license = calulationCriteria.getTradelicense();

      BillingSlabSearchCriteria searchCriteria = new BillingSlabSearchCriteria();
      searchCriteria.setTenantId(license.getTenantId());
      searchCriteria.setStructureType(license.getTradeLicenseDetail().getStructureType());
      searchCriteria.setLicenseType(license.getLicenseType().toString());

      BigDecimal tradeUnitFee = getTradeUnitFee(license);
      BigDecimal accessoryFee = new BigDecimal(0);

      if(!CollectionUtils.isEmpty(license.getTradeLicenseDetail().getAccessories())){
           accessoryFee = getAccessoryFee(license);
      }

      TaxHeadEstimate estimate = new TaxHeadEstimate();
      BigDecimal totalTax = tradeUnitFee.add(accessoryFee);

      /*if(license.getTradeLicenseDetail().getAdhocExemption()!=null)
          totalTax = totalTax.subtract(license.getTradeLicenseDetail().getAdhocExemption());
      if(license.getTradeLicenseDetail().getAdhocPenalty()!=null)
          totalTax = totalTax.add(license.getTradeLicenseDetail().getAdhocPenalty());*/

      if(totalTax.compareTo(BigDecimal.ZERO)==-1)
          throw new CustomException("INVALID AMOUNT","Tax amount is negative");

      estimate.setEstimateAmount(totalTax);
      estimate.setCategory(Category.TAX);
      estimate.setTaxHeadCode(config.getBaseTaxHead());
      return estimate;
  }


  private TaxHeadEstimate getAdhocPenalty(CalulationCriteria calulationCriteria){
      TradeLicense license = calulationCriteria.getTradelicense();
      TaxHeadEstimate estimate = new TaxHeadEstimate();
      estimate.setEstimateAmount(license.getTradeLicenseDetail().getAdhocPenalty());
      estimate.setTaxHeadCode(config.getAdhocPenaltyTaxHead());
      estimate.setCategory(Category.PENALTY);
      return estimate;
  }


    private TaxHeadEstimate getAdhocExemption(CalulationCriteria calulationCriteria){
        TradeLicense license = calulationCriteria.getTradelicense();
        TaxHeadEstimate estimate = new TaxHeadEstimate();
        estimate.setEstimateAmount(license.getTradeLicenseDetail().getAdhocExemption());
        estimate.setTaxHeadCode(config.getAdhocExemptionTaxHead());
        estimate.setCategory(Category.EXEMPTION);
        return estimate;
    }




  private BigDecimal getTradeUnitFee(TradeLicense license){

      BigDecimal tradeUnitTotalFee = new BigDecimal(0);
      List<TradeUnit> tradeUnits = license.getTradeLicenseDetail().getTradeUnits();

       for(TradeUnit tradeUnit : tradeUnits)
       {
          List<Object> preparedStmtList = new ArrayList<>();
          BillingSlabSearchCriteria searchCriteria = new BillingSlabSearchCriteria();
          searchCriteria.setTenantId(license.getTenantId());
          searchCriteria.setStructureType(license.getTradeLicenseDetail().getStructureType());
          searchCriteria.setLicenseType(license.getLicenseType().toString());
          searchCriteria.setTradeType(tradeUnit.getTradeType());
          searchCriteria.setType(config.getBillingSlabRateType());
          if(tradeUnit.getUomValue()!=null)
          {
              searchCriteria.setUomValue(Double.parseDouble(tradeUnit.getUomValue()));
              searchCriteria.setUom(tradeUnit.getUom());
          }
          // Call the Search
          String query = queryBuilder.getSearchQuery(searchCriteria, preparedStmtList);
          log.info("query "+query);
          log.info("preparedStmtList "+preparedStmtList.toString());
          List<BillingSlab> billingSlabs = repository.getDataFromDB(query, preparedStmtList);

          if(billingSlabs.size()>1)
              throw new CustomException("BILLINGSLAB ERROR","Found multiple BillingSlabs for the given TradeType");
          if(CollectionUtils.isEmpty(billingSlabs))
              throw new CustomException("BILLINGSLAB ERROR","No BillingSlab Found for the given tradeType");

           tradeUnitTotalFee = tradeUnitTotalFee.add(billingSlabs.get(0).getRate());
      }

      return tradeUnitTotalFee;
  }


  private BigDecimal getAccessoryFee(TradeLicense license){

      BigDecimal accessoryTotalFee = new BigDecimal(0);
      List<Accessory> accessories = license.getTradeLicenseDetail().getAccessories();
       for(Accessory accessory : accessories)
       {
           List<Object> preparedStmtList = new ArrayList<>();
           BillingSlabSearchCriteria searchCriteria = new BillingSlabSearchCriteria();
           searchCriteria.setTenantId(license.getTenantId());
           searchCriteria.setAccessoryCategory(accessory.getAccessoryCategory());
          if(accessory.getUomValue()!=null)
          {
              searchCriteria.setUomValue(Double.parseDouble(accessory.getUomValue()));
              searchCriteria.setUom(accessory.getUom());
          }
          // Call the Search
          String query = queryBuilder.getSearchQuery(searchCriteria, preparedStmtList);
           log.info("query "+query);
           log.info("preparedStmtList "+preparedStmtList.toString());
          List<BillingSlab> billingSlabs = repository.getDataFromDB(query, preparedStmtList);

          if(billingSlabs.size()>1)
              throw new CustomException("BILLINGSLAB ERROR","Found multiple BillingSlabs for the given accessories ");
          if(CollectionUtils.isEmpty(billingSlabs))
              throw new CustomException("BILLINGSLAB ERROR","No BillingSlab Found for the given accessory");

           accessoryTotalFee = accessoryTotalFee.add(billingSlabs.get(0).getRate());
      }
      return accessoryTotalFee;
  }









}
