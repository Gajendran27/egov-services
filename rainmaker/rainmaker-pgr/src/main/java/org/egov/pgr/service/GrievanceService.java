package org.egov.pgr.service;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.pgr.contract.AuditDetails;
import org.egov.pgr.contract.IdResponse;
import org.egov.pgr.producer.PGRProducer;
import org.egov.pgr.repository.IdGenRepo;
import org.egov.pgr.utils.PGRConstants;
import org.egov.pgr.utils.PGRUtils;
import org.egov.pgr.utils.ResponseInfoFactory;
import org.egov.pgr.v2.contract.ActionInfo;
import org.egov.pgr.v2.contract.Comment;
import org.egov.pgr.v2.contract.Media;
import org.egov.pgr.v2.contract.Service;
import org.egov.pgr.v2.contract.ServiceRequest;
import org.egov.pgr.v2.contract.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@Slf4j
public class GrievanceService {

		@Value("${kafka.topics.save.servicereq}")
		private String saveTopic;

		@Value("${kafka.topics.update.servicereq}")
		private String updateTopic;
		
		@Value("${kafka.topics.notification.complaint}")
		private String complaintTopic;

		@Autowired
		private ResponseInfoFactory factory;

		@Autowired
		private IdGenRepo idGenRepo;

		@Autowired
		private PGRUtils pGRUtils;
		
		@Autowired
		private PGRProducer pGRProducer;
		
		private static final String MODULE_NAME = "PGR:";

		/***
		 * Asynchronous method performs business logic if any and adds the data to
		 * persister queue on create topic
		 * 
		 * @param request
		 */
		public ServiceResponse create(ServiceRequest request) {

			log.debug(" the incoming request obj in service : {}", request);

			RequestInfo requestInfo = request.getRequestInfo();
			List<Service> serviceReqs = request.getServices();
			List<ActionInfo> actionInfos = request.getActionInfo();
			String tenantId = serviceReqs.get(0).getTenantId();
			Integer servReqLen = serviceReqs.size();

			List<String> servReqIdList = getIdList(requestInfo, tenantId, servReqLen, PGRConstants.SERV_REQ_ID_NAME,
					PGRConstants.SERV_REQ_ID_FORMAT);
			AuditDetails auditDetails = pGRUtils.getAuditDetails(String.valueOf(requestInfo.getUserInfo().getId()));
			
			for (int servReqCount = 0; servReqCount < servReqLen; servReqCount++) {

				Service servReq = serviceReqs.get(servReqCount);
				ActionInfo actionInfo = actionInfos.get(servReqCount);
				String currentId = servReqIdList.get(servReqCount);
				servReq.setAuditDetails(auditDetails);
				servReq.setServiceRequestId(currentId);
				actionInfo.setServiceRequestId(MODULE_NAME + currentId);
				setIdsForSubList(actionInfo.getMedia(), actionInfo.getComments(), true, requestInfo,
						auditDetails.getCreatedTime());
			}
			
			pGRProducer.push(saveTopic, request);
			pGRProducer.push(complaintTopic, request);
			return getServiceResponse(request);
		}

		/***
		 * Asynchronous method performs business logic if any and adds the data to
		 * persister queue on update topic
		 * 
		 * @param request
		 */
		public ServiceResponse update(ServiceRequest request) {

			log.debug(" the incoming request obj in service : {}", request);

			List<Service> serviceReqs = request.getServices();
			List<ActionInfo> actionInfos = request.getActionInfo();

			final AuditDetails auditDetails = pGRUtils.getAuditDetails(String.valueOf(request.getRequestInfo().getUserInfo().getId()));
			int serviceLen = serviceReqs.size();
			for(int index=0;index<serviceLen;index++) {
				
				Service servReq = serviceReqs.get(index);
				ActionInfo actionInfo = actionInfos.get(index);
				servReq.setAuditDetails(auditDetails);
				setIdsForSubList(actionInfo.getMedia(), actionInfo.getComments(), false, request.getRequestInfo(),auditDetails.getLastModifiedTime());
			}

			pGRProducer.push(updateTopic, request);
			pGRProducer.push(complaintTopic, request);
			return getServiceResponse(request);
		}

		/**
		 * to filter the sublist object for idgeneration if they are null
		 * 
		 * @param mediaList
		 * @param commentsList
		 */
		private void setIdsForSubList(List<Media> mediaList, List<Comment> commentsList, Boolean isCreate, RequestInfo requestInfo,Long when) {

			User user = requestInfo.getUserInfo();
			Role role = user.getRoles().get(0);

			String by = user.getId() + "-" + role.getName();

			if (null != mediaList)
				mediaList.forEach(media -> {
					if (null == media.getUuid() || isCreate) {
						media.setUuid(UUID.randomUUID().toString());
						media.setBy(by);
						media.setWhen(when);
					}
				});

			if (null != commentsList)
				commentsList.forEach(comment -> {
					if (null == comment.getUuid() || isCreate) {
						comment.setUuid(UUID.randomUUID().toString());
						comment.setBy(by);
						comment.setWhen(when);
					}
				});
		}


		/**
		 * method to parse the IdGenResponse from IdgenRepo to List of String ids
		 * required by the respective methods
		 * 
		 * @param requestInfo
		 * @param tenantId
		 * @param length
		 * @param idKey
		 * @param idformat
		 * 
		 */
		private List<String> getIdList(RequestInfo requestInfo, String tenantId, Integer length, String idKey,
				String idformat) {

			return idGenRepo.getId(requestInfo, tenantId, length, idKey, idformat).getIdResponses().stream()
					.map(IdResponse::getId).collect(Collectors.toList());
		}

		/**
		 * returns ServiceResponse built based on the given ServiceRequest
		 * 
		 * @param serviceReqRequest
		 * @return serviceReqResponse
		 */
		public ServiceResponse getServiceResponse(ServiceRequest serviceReqRequest) {

			return ServiceResponse.builder()
					.responseInfo(factory.createResponseInfoFromRequestInfo(serviceReqRequest.getRequestInfo(), true))
					.services(serviceReqRequest.getServices()).build();
		}
}
