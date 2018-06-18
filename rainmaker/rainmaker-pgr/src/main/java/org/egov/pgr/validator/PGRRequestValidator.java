package org.egov.pgr.validator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.pgr.contract.ServiceReqSearchCriteria;
import org.egov.pgr.contract.ServiceRequest;
import org.egov.pgr.contract.ServiceResponse;
import org.egov.pgr.model.ActionInfo;
import org.egov.pgr.model.Service;
import org.egov.pgr.repository.ServiceRequestRepository;
import org.egov.pgr.service.GrievanceService;
import org.egov.pgr.utils.ErrorConstants;
import org.egov.pgr.utils.PGRConstants;
import org.egov.pgr.utils.PGRUtils;
import org.egov.pgr.utils.WorkFlowConfigs;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@Slf4j
public class PGRRequestValidator {

	@Autowired
	private GrievanceService requestService;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private PGRUtils pgrUtils;

	@Value("${egov.mdms.host}")
	private String mdmsHost;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsEndpoint;

	/**
	 * validates the create Request based on the following cirtera:
	 * 
	 * 1. Checks if the length of actionInfo and services are same.
	 * 2. Overrides accountId field of the request with the id of the logged in user.
	 * 3. Services codes mentioned in the request are validated against the mdms records.
	 * 4. Validates address. 
	 * 
	 * @param serviceRequest
	 */
	public void validateCreate(ServiceRequest serviceRequest) {

		Map<String, String> errorMap = new HashMap<>();

		validateIfArraysEqual(serviceRequest, errorMap);
		overRideCitizenAccountId(serviceRequest);
		vaidateServiceCodes(serviceRequest, errorMap);
		validateAddressCombo(serviceRequest, errorMap);

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
	}

	/**
	 * validates the update Request based on the following cirtera:
	 * 
	 * 1. Checks if the length of actionInfo and services are same.
	 * 2. Overrides accountId field of the request with the id of the logged in user.
	 * 3. Services codes mentioned in the request are validated against the mdms records.
	 * 4. Validates assignment of service requests based on various roles.
	 * 5. Validates the action performed on the service request based on the role.
	 * 6. Checks if the service being updated does exist.
	 *
	 * @param serviceRequest
	 */
	public void validateUpdate(ServiceRequest serviceRequest) {

		Map<String, String> errorMap = new HashMap<>();

		validateIfArraysEqual(serviceRequest, errorMap);
		vaidateServiceCodes(serviceRequest, errorMap);
		overRideCitizenAccountId(serviceRequest);
		validateAssignments(serviceRequest, errorMap);
		validateAction(serviceRequest, errorMap);
		validateIfServicesExists(serviceRequest, errorMap);

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
	}

	/**
	 * validates the services by fetching the service ids from the database
	 * 
	 * @param serviceRequest
	 * @param errorMap
	 */
	private void validateIfServicesExists(ServiceRequest serviceRequest, Map<String, String> errorMap) {
		ServiceReqSearchCriteria serviceReqSearchCriteria = ServiceReqSearchCriteria.builder()
				.tenantId(serviceRequest.getServices().get(0).getTenantId()).serviceRequestId(serviceRequest
						.getServices().stream().map(Service::getServiceRequestId).collect(Collectors.toList()))
				.build();

		Map<String, Service> map = ((ServiceResponse) requestService
				.getServiceRequestDetails(serviceRequest.getRequestInfo(), serviceReqSearchCriteria)).getServices()
						.stream().collect(Collectors.toMap(Service::getServiceRequestId, Function.identity()));

		List<String> errorList = new ArrayList<>();
		serviceRequest.getServices().forEach(a -> {

			if (map.get(a.getServiceRequestId()) == null)
				errorList.add(a.getServiceRequestId());
		});

		if (!errorList.isEmpty())
			errorMap.put(ErrorConstants.INVALID_SERVICEREQUESTID_CODE, ErrorConstants.INVALID_SERVICEREQUESTID_MSG + errorList);
	}

	/**
	 * checks the length of both service and actionInfo array to avoid mismatches in
	 * between them
	 * 
	 * @param serviceRequest
	 * @param errorMap
	 */
	private void validateIfArraysEqual(ServiceRequest serviceRequest, Map<String, String> errorMap) {
		if (null != serviceRequest.getActionInfo()
				&& serviceRequest.getServices().size() != serviceRequest.getActionInfo().size())
			errorMap.put(ErrorConstants.UNEQUAL_REQUEST_SIZE_KEY, ErrorConstants.UNEQUAL_REQUEST_SIZE_MSG);
	}

	/**
	 * validates the presence of any of the combination of address
	 * parameters(atleast one of the parameters is mandatory)
	 * 
	 * @param serviceRequest
	 * @param errorMap
	 */
	private void validateAddressCombo(ServiceRequest serviceRequest, Map<String, String> errorMap) {

		if (serviceRequest.getServices().size() != serviceRequest.getServices().parallelStream()
				.filter(a -> (a.getAddress() != null || a.getAddressId() != null
						|| (a.getLat() != null && a.getLong() != null)))
				.map(Service::getServiceRequestId).collect(Collectors.toList()).size())
			errorMap.put(ErrorConstants.CREATE_ADDRESS_COMBO_ERROR_KEY, ErrorConstants.CREATE_ADDRESS_COMBO_ERROR_MSG);
	}

	/**
	 * validates if the given service codes in the request objects are admissible
	 * 
	 * @param serviceRequest
	 * @param errorMap
	 */
	private void vaidateServiceCodes(ServiceRequest serviceRequest, Map<String, String> errorMap) {

		String tenantId = serviceRequest.getServices().get(0).getTenantId();
		List<String> serviceCodes = getServiceCodes(tenantId,
				serviceRequest.getServices().parallelStream().map(Service::getServiceCode).collect(Collectors.toSet()),
				serviceRequest.getRequestInfo());

		List<String> errorList = new ArrayList<>();
		serviceRequest.getServices().forEach(a -> {

			if (!serviceCodes.contains(a.getServiceCode()))
				errorList.add(a.getServiceCode());
		});

		if (!errorList.isEmpty())
			errorMap.put(ErrorConstants.INVALID_SERVICECODE_CODE, ErrorConstants.INVALID_SERVICECODE_MSG + errorList);
	}

	/**
	 * helper method which collects the service code from services obtained by
	 * databse call
	 * 
	 * @param tenantId
	 * @param inputCodes
	 * @param requestInfo
	 * @return
	 */
	private List<String> getServiceCodes(String tenantId, Set<String> inputCodes, RequestInfo requestInfo) {

		StringBuilder uri = new StringBuilder(mdmsHost).append(mdmsEndpoint);
		MdmsCriteriaReq criteriaReq = pgrUtils.prepareMdMsRequest(tenantId.split("\\.")[0], PGRConstants.SERVICE_CODES,
				inputCodes.toString(), requestInfo);
		try {
			Object result = serviceRequestRepository.fetchResult(uri, criteriaReq);
			return JsonPath.read(result, PGRConstants.JSONPATH_SERVICE_CODES);
		} catch (Exception e) {
			throw new CustomException(ErrorConstants.INVALID_TENANT_ID_MDMS_SERVICE_CODE_KEY,
					ErrorConstants.INVALID_TENANT_ID_MDMS_SERVICE_CODE_MSG);
		}
	}

	/**
	 * overrides the accoundId field with the current user Id if the user is a
	 * CITIZEN
	 * 
	 * @param serviceRequest
	 */
	private void overRideCitizenAccountId(ServiceRequest serviceRequest) {

		User user = serviceRequest.getRequestInfo().getUserInfo();
		List<String> codes = user.getRoles().parallelStream().map(Role::getName).collect(Collectors.toList());
		boolean isUserCitizen = codes.contains("CITIZEN") || codes.contains("Citizen");
		if (isUserCitizen)
			serviceRequest.getServices().forEach(service -> service.setAccountId(String.valueOf(user.getId())));
	}

	/**
	 * validates if the assignee has been given for proper action and not provided
	 * for inappropriate actions
	 * 
	 * @param serviceRequest
	 * @param errorMap
	 */
	private void validateAssignments(ServiceRequest serviceRequest, Map<String, String> errorMap) {

		List<String> errorMsgForActionAssign = new ArrayList<>();
		List<Service> services = serviceRequest.getServices();

		List<ActionInfo> infos = serviceRequest.getActionInfo();

		if (null != infos)
			for (int i = 0; i <= infos.size() - 1; i++) {
				ActionInfo info = infos.get(i);
				if (null != info && null != info.getAction())
					if ((WorkFlowConfigs.ACTION_ASSIGN.equalsIgnoreCase(info.getAction())
							|| WorkFlowConfigs.ACTION_REASSIGN.equalsIgnoreCase(info.getAction()))
							&& info.getAssignee() == null)
						errorMsgForActionAssign.add(services.get(i).getServiceRequestId());
					else if (!WorkFlowConfigs.ACTION_ASSIGN.equalsIgnoreCase(info.getAction())
							&& !WorkFlowConfigs.ACTION_REASSIGN.equalsIgnoreCase(info.getAction())
							&& null != info.getAssignee())
						info.setAssignee(null);
			}

		if (!errorMsgForActionAssign.isEmpty())
			errorMap.put(ErrorConstants.ASSIGNEE_MISSING_FOR_ACTION_ASSIGN_REASSIGN_KEY,
					ErrorConstants.ASSIGNEE_MISSING_FOR_ACTION_ASSIGN_REASSIGN_MSG + errorMsgForActionAssign);
	}

	/**
	 * validates the legality of the search criteria given
	 * 
	 * @param criteria
	 * @param requestInfo
	 */
	public void validateSearch(ServiceReqSearchCriteria criteria, RequestInfo requestInfo) {
		log.info("Validating search request...");
		Map<String, String> errorMap = new HashMap<>();
		validateUserRBACProxy(errorMap, requestInfo);
		if ((criteria.getStartDate() != null && criteria.getStartDate() > new Date().getTime())
				|| (criteria.getEndDate() != null && criteria.getEndDate() > new Date().getTime())) {
			errorMap.put(ErrorConstants.INVALID_START_END_DATE_CODE, ErrorConstants.INVALID_START_END_DATE_MSG);
		}
		if ((criteria.getStartDate() != null && criteria.getEndDate() != null)
				&& criteria.getStartDate().compareTo(criteria.getEndDate()) > 0) {
			errorMap.put(ErrorConstants.INVALID_START_DATE_CODE, ErrorConstants.INVALID_START_DATE_MSG);
		}

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);

		log.info("All Validations passed!");
	}

	public void validateUserRBACProxy(Map<String, String> errorMap, RequestInfo requestInfo) {

		if (null != requestInfo.getUserInfo()) {
			if (null == requestInfo.getUserInfo().getType() || requestInfo.getUserInfo().getType().isEmpty()) {
				errorMap.put(ErrorConstants.MISSING_USERTYPE_CODE, ErrorConstants.MISSING_USERTYPE_MSG);
				return;
			}
			if (null == requestInfo.getUserInfo().getId() || (null == requestInfo.getUserInfo().getRoles()
					|| requestInfo.getUserInfo().getRoles().isEmpty())) {
				errorMap.put(ErrorConstants.MISSING_ROLE_USERID_CODE, ErrorConstants.MISSING_ROLE_USERID_MSG);
				return;
			}
		} else {
			errorMap.put(ErrorConstants.MISSING_USERINFO_CODE, ErrorConstants.MISSING_USERINFO_MSG);
			return;
		}

	}

	/**
	 * validates of the action can be peformed by the user with the given role
	 * 
	 * @param serviceRequest
	 * @param errorMap
	 */
	public void validateAction(ServiceRequest serviceRequest, Map<String, String> errorMap) {
		Map<String, List<String>> roleActionMap = WorkFlowConfigs.getRoleActionMap();
		List<String> roles = serviceRequest.getRequestInfo().getUserInfo().getRoles().parallelStream()
				.map(Role::getName).collect(Collectors.toList());
		List<String> actions = null;
		if (roles.contains("Citizen") || roles.contains("CITIZEN"))
			actions = roleActionMap.get(serviceRequest.getRequestInfo().getUserInfo().getRoles().get(0).getName().toUpperCase());
		else
			actions = roleActionMap.get(pgrUtils.getPrecedentRole(serviceRequest.getRequestInfo().getUserInfo()
					.getRoles().parallelStream().map(Role::getName).collect(Collectors.toList())).toUpperCase());
		log.info("actions: " + actions);
		if (null != actions && !actions.isEmpty()) {
			List<ActionInfo> infos = serviceRequest.getActionInfo();
			if (!CollectionUtils.isEmpty(infos))
				for (int i = 0; i <= infos.size() - 1; i++) {
					ActionInfo info = infos.get(i);
					if (null != info && null != info.getAction() && !info.getAction().isEmpty()
							&& !actions.contains(info.getAction())) {
						errorMap.put(ErrorConstants.INVALID_ROLE_CODE,
								"Invalid Action: " + info.getAction() + " " + "for Role: " + serviceRequest
										.getRequestInfo().getUserInfo().getRoles().get(0).getName().toUpperCase());
					}
				}
		} else {
			errorMap.put(ErrorConstants.INVALID_ROLE_CODE, ErrorConstants.INVALID_ROLE_MSG
					+ serviceRequest.getRequestInfo().getUserInfo().getRoles().get(0).getName().toUpperCase());
		}

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);
	}
}
