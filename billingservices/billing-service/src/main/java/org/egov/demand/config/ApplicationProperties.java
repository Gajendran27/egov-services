/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) 2016  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.demand.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import lombok.Getter;
import lombok.ToString;

@Configuration
@Getter
@PropertySource(value = { "classpath:config/application-config.properties" }, ignoreResourceNotFound = true)
@ToString
public class ApplicationProperties {

	@Value("${egov.services.user_service.hostname}")
	private String userServiceHostName;

	@Value("${egov.services.user_service.searchpath}")
	private String userServiceSearchPath;

	private static final String SEARCH_PAGESIZE_DEFAULT = "search.pagesize.default";

	@Autowired
	private Environment environment;

	@Value("${kafka.topics.save.bill}")
	private String createBillTopic;

	@Value("${kafka.topics.update.bill}")
	private String updateBillTopic;

	@Value("${kafka.topics.save.bill.key}")
	private String createBillTopicKey;

	@Value("${kafka.topics.update.bill.key}")
	private String updatekBillTopicKey;

	//Added for Creatining taxHeadMaster
	@Value("${kafka.topics.save.taxHeadMaster}")
	private String createTaxHeadMasterTopicName;

	@Value("${kafka.topics.update.taxHeadMaster}")
	private String updateTaxHeadMasterTopicName;

	@Value("${kafka.topics.save.taxHeadMaster.key}")
	private String createTaxHeadMasterTopicKey;

	@Value("${kafka.topics.update.taxHeadMaster.key}")
	private String updateTaxHeadMasterTopicKey;

	//Added for GlCodeMaster
	@Value("${kafka.topics.save.glCodeMaster}")
	private String createGlCodeMasterTopicName;
	
	@Value("${kafka.topics.save.glCodeMaster.key}")
	private String createGlCodeMasterTopicKey;
	
	@Value("${kafka.topics.create.taxperiod.name}")
	private String createTaxPeriodTopicName;

	@Value("${kafka.topics.update.taxperiod.name}")
	private String updateTaxPeriodTopicName;

	@Value("${kafka.topics.create.taxperiod.key}")
	private String createTaxPeriodTopicKey;

	@Value("${kafka.topics.update.taxperiod.key}")
	private String updateTaxPeriodTopicKey;

	@Value("${kafka.topics.create.businessservicedetail.name}")
	private String createBusinessServiceDetailTopicName;

	@Value("${kafka.topics.update.businessservicedetail.name}")
	private String updateBusinessServiceDetailTopicName;

	@Value("${kafka.topics.create.businessservicedetail.key}")
	private String createBusinessServiceDetailTopicKey;

	@Value("${kafka.topics.update.businessservicedetail.key}")
	private String updateBusinessServiceDetailTopicKey;

	@Value("${bs.bill.seq.name}")
	private String billSeqName;

	@Value("${bs.billdetail.seq.name}")
	private String billDetailSeqName;

	@Value("${bs.billaccountdetail.seq.name}")
	private String billAccDetailSeqName;

	@Value("${kafka.topics.save.demand}")
	private String createDemandTopic;

	@Value("${kafka.topics.update.demand}")
	private String updateDemandTopic;

	@Value("${bs.demand.seq.name}")
	private String demandSeqName;

	@Value("${bs.demanddetail.seq.name}")
	private String demandDetailSeqName;

	@Value("${bs.billdetail.billnumber.seq.name}")
	private String billNumSeqName;

	@Value("${bs.taxhead.seq.name}")
	private String taxHeadSeqName;

	@Value("${bs.taxhead.code.seq.name}")
	private String taxHeadCodeSeqName;
	
	@Value("${bs.glcodehead.seq.name}")
	private String glCodeMasterseqName;

	@Value("${bs.taxperiod.seq.name}")
	private String taxPeriodSeqName;

	@Value("${bs.businessservicedetail.seq.name}")
	private String businessServiceDetailSeqName;

	public String commonsSearchPageSizeDefault() {
		return environment.getProperty(SEARCH_PAGESIZE_DEFAULT);
	}
}