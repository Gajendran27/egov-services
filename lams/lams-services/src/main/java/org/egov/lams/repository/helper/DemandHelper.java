package org.egov.lams.repository.helper;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.egov.lams.config.PropertiesManager;
import org.egov.lams.model.Agreement;
import org.egov.lams.model.enums.Action;
import org.egov.lams.service.AgreementService;
import org.egov.lams.web.contract.AgreementRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DemandHelper {

	public static final Logger logger = LoggerFactory.getLogger(AgreementService.class);

	@Autowired
	private PropertiesManager propertiesManager;

	public String getDemandReasonUrlParams(AgreementRequest agreementRequest, String taxReason, Date date) {

		Agreement agreement = agreementRequest.getAgreement();

		logger.info("the criteria for demandReasonSearch are ::: " + "?moduleName="
				+ propertiesManager.getDemandModuleName() + "&taxPeriod=" + agreement.getTimePeriod() + "&fromDate="
				+ agreement.getCommencementDate() + "&toDate=" + date + "&installmentType="
				+ agreement.getPaymentCycle().toString() + "&taxCategory=" + propertiesManager.getTaxCategoryName());
		Date fromDate;
		if(Action.RENEWAL.equals(agreement.getAction()))
			fromDate = agreement.getRenewalDate();
		else
			fromDate = agreement.getCommencementDate();
		StringBuilder urlParams = new StringBuilder();
		urlParams.append("?moduleName=" + propertiesManager.getDemandModuleName());
		urlParams.append("&taxPeriod=" + agreement.getTimePeriod());
		urlParams.append("&fromDate=" + DateFormatUtils.format(fromDate, "dd/MM/yyyy"));
		urlParams.append("&installmentType=" + agreement.getPaymentCycle().toString());
		if ("STATE_GST".equalsIgnoreCase(taxReason) || "CENTRAL_GST".equalsIgnoreCase(taxReason)) {
			urlParams.append("&taxCategory=GST");
		} else if ("SERVICE_TAX".equalsIgnoreCase(taxReason)) {
			urlParams.append("&taxCategory=SERVICETAX");
		} else
			urlParams.append("&taxCategory=" + (propertiesManager.getTaxReasonPenalty().equalsIgnoreCase(taxReason)
					? propertiesManager.getPenaltyCategoryName() : propertiesManager.getTaxCategoryName()));

		urlParams.append("&tenantId=" + agreement.getTenantId());
		urlParams.append("&taxReason=" + taxReason);
		urlParams.append("&toDate=" + DateFormatUtils.format(date, "dd/MM/yyyy"));
		return urlParams.toString();
	}
	
	public String getDemandIdParams(List<Long> idList) {
		String dmdIdString = null;
		if (!idList.isEmpty()) {
			dmdIdString = idList.stream().map(Object::toString).collect(Collectors.joining(","));
		}
		return dmdIdString;
	}
}
