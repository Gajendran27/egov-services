package org.egov.pgr.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.pgr.contract.CountResponse;
import org.egov.pgr.contract.IdGenerationResponse;
import org.egov.pgr.contract.IdResponse;
import org.egov.pgr.contract.ServiceReq;
import org.egov.pgr.contract.ServiceReqRequest;
import org.egov.pgr.contract.ServiceReqResponse;
import org.egov.pgr.contract.ServiceReqSearchCriteria;
import org.egov.pgr.repository.IdGenRepo;
import org.egov.pgr.repository.ServiceRequestRepository;
import org.egov.pgr.utils.PGRConstants;
import org.egov.pgr.utils.ResponseInfoFactory;
import org.egov.tracer.kafka.LogAwareKafkaTemplate;
import org.egov.tracer.model.ServiceCallException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(MockitoJUnitRunner.class)
public class ServiceRequestServiceTest {
	
	@Mock
	private ServiceRequestRepository serviceRequestRepository;
	
	@Mock
	private LogAwareKafkaTemplate< String, Object> kafkaProducer;
	
	@Mock
	private ResponseInfoFactory factory;

	@Mock
	private IdGenRepo idGenRepo;
	
	private String saveTopic = "save-pgr-servicereq";

	private String updateTopic = "update-pgr-servicereq";
	
	private String tenantId = "default";
	
	@InjectMocks
	private ServiceRequestService service;
	
	@Before
	public void before() {
		
		ReflectionTestUtils.setField(service, "saveTopic", "save-pgr-servicereq");
		ReflectionTestUtils.setField(service, "updateTopic", "update-pgr-servicereq");
	}
	
	@Test
	public void createShouldSucceed() {
	
		ServiceReqRequest serviceReqRequest = getServiceReqRequest();
		RequestInfo requestInfo = serviceReqRequest.getRequestInfo();
		ServiceReqResponse serviceReqResponse = getServiceReqResponseForCreate();
		ResponseInfo responseInfo = serviceReqResponse.getResponseInfo();
		
		Boolean boolVal =  true;
		
		int longVal = serviceReqRequest.getServiceReq().size();
		
		
		Mockito.when(idGenRepo.getId(requestInfo, tenantId, longVal, PGRConstants.SERV_REQ_ID_NAME,
				PGRConstants.SERV_REQ_ID_FORMAT)).thenReturn(getIdGenResponse());
		Mockito.when(factory.createResponseInfoFromRequestInfo(requestInfo, boolVal)).thenReturn(responseInfo);
		Mockito.when(kafkaProducer.send(saveTopic, serviceReqRequest)).thenReturn(null);

		assertTrue(serviceReqResponse.equals(service.create(serviceReqRequest)));
	}
	
	@Test
	public void updateShouldSucceed() {
	
		ServiceReqRequest serviceReqRequest = getServiceReqRequest();
		RequestInfo requestInfo = serviceReqRequest.getRequestInfo();
		ServiceReqResponse serviceReqResponse = getServiceReqResponseForUpdate();
		ResponseInfo responseInfo = serviceReqResponse.getResponseInfo();
		
		Boolean boolVal =  true;
		
		Mockito.when(factory.createResponseInfoFromRequestInfo(requestInfo, boolVal)).thenReturn(responseInfo);
		Mockito.when(kafkaProducer.send(updateTopic, serviceReqRequest)).thenReturn(null);
		
		assertTrue(serviceReqResponse.equals(service.update(serviceReqRequest)));
	}
	
	private ServiceReqRequest getServiceReqRequest() {
		
		List<ServiceReq> reqs = new ArrayList<>();
		reqs.add(ServiceReq.builder().tenantId(tenantId).build());
		return ServiceReqRequest.builder().serviceReq(reqs).requestInfo(new RequestInfo()).build();
	}
	
	private ServiceReqResponse getServiceReqResponseForCreate() {
		
		List<ServiceReq> reqs = new ArrayList<>();
		reqs.add(ServiceReq.builder().tenantId(tenantId).serviceRequestId(getId()).build());
		return ServiceReqResponse.builder().serviceReq(reqs).responseInfo(ResponseInfo.builder().resMsgId("uief87324").build()).build();
	}
	
	private ServiceReqResponse getServiceReqResponseForUpdate() {
		
		List<ServiceReq> reqs = new ArrayList<>();
		reqs.add(ServiceReq.builder().tenantId(tenantId).build());
		return ServiceReqResponse.builder().serviceReq(reqs).responseInfo(ResponseInfo.builder().resMsgId("uief87324").build()).build();
	}
	
	private String getId(){
		
		return "id1";
	}
	
	private IdGenerationResponse getIdGenResponse() {

		List<IdResponse> idResponses = new ArrayList<>();
		IdResponse idResponse = new IdResponse();
		idResponse.setId(getId());
		idResponses.add(idResponse);
		return IdGenerationResponse.builder().idResponses(idResponses).build();
	}
	
	@Test
	public void testGetServiceRequestsSuccess() {
		Object response = null;
		RequestInfo requestInfo = Mockito.mock(RequestInfo.class);
		ServiceReqSearchCriteria serviceReqSearchCriteria = Mockito.mock(ServiceReqSearchCriteria.class);
		ServiceReqResponse serviceReqResponse = new ServiceReqResponse();
		Mockito.when(serviceRequestRepository.getServiceRequests(requestInfo, serviceReqSearchCriteria))
		.thenReturn(serviceReqResponse);
		response = service.getServiceRequests(requestInfo, serviceReqSearchCriteria);
		
		assertNotNull(response);
		
		
	}
	
	@Test
	public void testGetServiceRequestsFailure() {
		ObjectMapper mapper = new ObjectMapper();
		Object response = new Object();
		RequestInfo requestInfo = Mockito.mock(RequestInfo.class);
		ServiceReqSearchCriteria serviceReqSearchCriteria = Mockito.mock(ServiceReqSearchCriteria.class);
		Mockito.when(serviceRequestRepository.getServiceRequests(requestInfo, serviceReqSearchCriteria))
		.thenReturn(null);
		response = service.getServiceRequests(requestInfo, serviceReqSearchCriteria);
		ServiceReqResponse serviceReqResponse = mapper.convertValue(response, ServiceReqResponse.class);

		assertTrue(serviceReqResponse.getServiceReq().isEmpty());
		
		
	}
	
	@Test(expected = ServiceCallException.class)
	public void testGetServiceRequestsException() {
		RequestInfo requestInfo = Mockito.mock(RequestInfo.class);
		ServiceReqSearchCriteria serviceReqSearchCriteria = Mockito.mock(ServiceReqSearchCriteria.class);
		Mockito.when(serviceRequestRepository.getServiceRequests(requestInfo, serviceReqSearchCriteria))
		.thenThrow(new ServiceCallException());
		
		service.getServiceRequests(requestInfo, serviceReqSearchCriteria);
				
	}
	
	@Test
	public void testGetCountSuccess() {
		Map<String, Double> innerMap= new HashMap<>();
		innerMap.put("count", 1D);
		Map<String, List<Object>> map = new HashMap<>();
		List<Object> list = new ArrayList<>();
		list.add(innerMap);
		map.put("count", list);
		RequestInfo requestInfo = Mockito.mock(RequestInfo.class);
		ServiceReqSearchCriteria serviceReqSearchCriteria = Mockito.mock(ServiceReqSearchCriteria.class);
		Mockito.when(serviceRequestRepository.getCount(requestInfo, serviceReqSearchCriteria))
		.thenReturn(map);
		Object res = service.getCount(requestInfo, serviceReqSearchCriteria);
		
		assertNotNull(res);
		
		
	}
	
	@Test
	public void testGetCountFailure() {
		ObjectMapper mapper = new ObjectMapper();
		RequestInfo requestInfo = Mockito.mock(RequestInfo.class);
		ServiceReqSearchCriteria serviceReqSearchCriteria = Mockito.mock(ServiceReqSearchCriteria.class);
		Mockito.when(serviceRequestRepository.getCount(requestInfo, serviceReqSearchCriteria))
		.thenReturn(null);
		Object res = service.getCount(requestInfo, serviceReqSearchCriteria);
		CountResponse response = mapper.convertValue(res, CountResponse.class);

		assertTrue(response.getCount() == 0D);
		
		
	}
	
	@Test(expected = Exception.class)
	public void testGetCountException() {
		Object response = new Object();
		RequestInfo requestInfo = Mockito.mock(RequestInfo.class);
		ServiceReqSearchCriteria serviceReqSearchCriteria = Mockito.mock(ServiceReqSearchCriteria.class);
		Mockito.when(serviceRequestRepository.getCount(requestInfo, serviceReqSearchCriteria))
		.thenReturn(response);
		
		service.getCount(requestInfo, serviceReqSearchCriteria);
		
				
	}

}
