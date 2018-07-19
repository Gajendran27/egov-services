package org.egov.pt.calculator.util;

import java.util.Arrays;
import java.util.List;

public class CalculatorConstants {

	/*
	 * tax head codes constants
	 */
	
	public static final String PT_TAX = "PT_TAX";
	
	public static final String PT_UNIT_USAGE_EXEMPTION = "PT_UNIT_USAGE_EXEMPTION";
	
	public static final String PT_OWNER_EXEMPTION = "PT_OWNER_EXEMPTION";
	
	public static final String PT_TIME_REBATE = "PT_TIME_REBATE";
	
	public static final String PT_TIME_PENALTY = "PT_TIME_PENALTY";
	
	public static final String PT_ADVANCE_CARRYFORWARD = "PT_ADVANCE_CARRYFORWARD";
	
	
	
	/*
	 * Mdms constants
	 */
	
	/*
	 * Master names
	 */
	
	public static final String USAGE_MAJOR_MASTER = "UsageCategoryMajor";

	public static final String USAGE_MINOR_MASTER = "UsageCategoryMinor";

	public static final String USAGE_SUB_MINOR_MASTER = "UsageCategorySubMinor";

	public static final String USAGE_DETAIL_MASTER = "UsageCategoryDetail";

	public static final String FINANCIAL_YEAR_MASTER = "FinancialYear";
	
	public static final String OWNER_TYPE_MASTER = "OwnerType";
	
	public static final String REBATE_MASTER = "Rebate";
	
	public static final String PENANLTY_MASTER = "Penalty";
	
	public static final List<String> propertyBasedExemptionMasterList = Arrays.asList(USAGE_MAJOR_MASTER, USAGE_MINOR_MASTER, USAGE_SUB_MINOR_MASTER, 
			USAGE_DETAIL_MASTER, OWNER_TYPE_MASTER);

	/*
	 * Module names
	 */
	
	public static final String FINANCIAL_MODULE = "egf-master";
	
	public static final String PROPERTY_TAX_MODULE = "PropertyTax";

	/*
	 * data field names  
	 */
	
	public static final String EXEMPTION_FIELD_NAME = "exemption";
	
	public static final String MAX_AMOUNT_FIELD_NAME = "maxAmount";
	
	public static final String MIN_AMOUNT_FIELD_NAME = "minAmount";
	
	public static final String FLAT_AMOUNT_FIELD_NAME = "flatAmount";
	
	public static final String RATE_FIELD_NAME = "rate";
	
	public static final String CODE_FIELD_NAME = "code";
	
	public static final String FROMFY_FIELD_NAME = "fromFY";
	
	public static final String ENDING_DATE_REBATE = "endingDay";
	
	public static final String STARTING_DATE_PENALTY = "startingDay";
	
	/*
	 * special charatcers
	 */
	
	public static final String UNDERSCORE = "_";
	
	/*
	 * Field Name constants
	 */
	public static final String MDMS_RESPONSE = "MdmsRes";

	public static final String FINANCIAL_YEAR_RANGE_FEILD_NAME = "finYearRange";

	public static final String BILLING_SLAB_MASTER = "BillingSlab";

	public static final String BILLING_SLAB_MODULE = "PropertyTax";

	public static final String FINANCIAL_YEAR_STARTING_DATE = "startingDate";

	public static final String FINANCIAL_YEAR_ENDING_DATE = "endingDate";

	public static final String PROPERTY_TAX_SERVICE_CODE = "PT";

	public static final String TENANT_ID_FIELD_FOR_SEARCH_URL = "tenantId=";

	public static final String SERVICE_FIELD_FOR_SEARCH_URL = "service=";
	
	public static final String BUSINESSSERVICE_FIELD_FOR_SEARCH_URL = "businessService=";
	
	public static final String SERVICE_FIELD_VALUE_PT = "PT";

	public static final String URL_PARAMS_SEPARATER = "?";

	public static final String SEPARATER = "&";
	
	/*
	 * billing service field names
	 */
	
	public static final String CONSUMER_CODE_SEARCH_FIELD_NAME = "consumerCode=";
	
	public static final String DEMAND_ID_SEARCH_FIELD_NAME = "demandId=";
	
	/*
	 * queries
	 */

	public static final String QUERY_ASSESSMENT_INSERT = "INSERT INTO eg_pt_assessment (uuid, assessmentnumber, assessmentyear, demandid,"
			
			+ " propertyid, tenantid, createdby, createdtime, lastmodifiedby, lastmodifiedtime)"

			+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	/*
	 * exceptions
	 */
	public static final String CONNECT_EXCEPTION_KEY = "CONNECTION_FAILED";
	
	public static final String EMPTY_DEMAND_ERROR_CODE = "EMPTY_DEMANDS";
	
	public static final String EMPTY_DEMAND_ERROR_MESSAGE = "No demands found for the given bill generate criteria";
	
	
	public static final String BILLING_SLAB_MATCH_ERROR_CODE = "NO_BILLING_SLAB_FOUND";
	
	public static final String BILLING_SLAB_MATCH_ERROR_MESSAGE = "No matching billing slab has been found for your criteria please check all your master fields";
}
