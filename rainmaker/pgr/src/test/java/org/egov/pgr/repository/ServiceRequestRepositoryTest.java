package org.egov.pgr.repository;

import static org.junit.Assert.*;

import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pgr.contract.SearcherRequest;
import org.egov.pgr.contract.ServiceReqSearchCriteria;
import org.egov.tracer.http.LogAwareRestTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource("classpath:application.properties")
public class ServiceRequestRepositoryTest {
	
	@Mock
	private LogAwareRestTemplate restTemplate;
	
	@InjectMocks
	private ServiceRequestRepository serviceRequestRepository;


	@Test
	public void testGetServiceRequestsFailure() {
		Object response = new Object();
		ReflectionTestUtils.setField(
				serviceRequestRepository, "searcherHost", "http://localhost:8093");
		ReflectionTestUtils.setField(
				serviceRequestRepository, "searcherEndpoint", "/infra-search/{moduleName}/{searchName}/_get");
		
		RequestInfo requestInfo = Mockito.mock(RequestInfo.class);
		ServiceReqSearchCriteria serviceReqSearchCriteria = Mockito.mock(ServiceReqSearchCriteria.class);
		SearcherRequest searcherRequest = Mockito.mock(SearcherRequest.class);

		Mockito.when(restTemplate.patchForObject("http://localhost:8093/infra-search/rainmaker-pgr/serviceRequestSearch/_get", 
				searcherRequest, Map.class)).thenReturn(null);
		
		response = serviceRequestRepository.getServiceRequests(requestInfo, serviceReqSearchCriteria);
				
		assertNull(response);
		
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testGetServiceRequestsSuccess() {
		ReflectionTestUtils.setField(
				serviceRequestRepository, "searcherHost", "http://localhost:8093");
		ReflectionTestUtils.setField(
				serviceRequestRepository, "searcherEndpoint", "/infra-search/{moduleName}/{searchName}/_get");
		
		RequestInfo requestInfo = new RequestInfo();
		ServiceReqSearchCriteria serviceReqSearchCriteria = new ServiceReqSearchCriteria();
		SearcherRequest searcherRequest = new SearcherRequest();
		searcherRequest.setRequestInfo(requestInfo);
		searcherRequest.setSearchCriteria(serviceReqSearchCriteria);
		serviceRequestRepository.getServiceRequests(requestInfo, serviceReqSearchCriteria);
				
        Mockito.verify(restTemplate).postForObject(
        		Matchers.any(String.class),
        		Matchers.any(SearcherRequest.class),
                Matchers.any(Class.class));
		
	}
	
	@SuppressWarnings("unchecked")
	@Test(expected = Exception.class)
	public void testGetServiceRequestsException() {
		RequestInfo requestInfo = Mockito.mock(RequestInfo.class);
		ServiceReqSearchCriteria serviceReqSearchCriteria = Mockito.mock(ServiceReqSearchCriteria.class);
		SearcherRequest searcherRequest = Mockito.mock(SearcherRequest.class);

		Mockito.when(restTemplate.patchForObject("http://localhost:8093/infra-search/rainmaker-pgr/serviceRequestSearch/_get", 
				searcherRequest, Map.class)).thenThrow(Exception.class);
		
		serviceRequestRepository.getServiceRequests(requestInfo, serviceReqSearchCriteria);
						
		
	}


}
