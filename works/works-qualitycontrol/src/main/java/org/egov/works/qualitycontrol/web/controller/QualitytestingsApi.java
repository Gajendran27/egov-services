/**
 * NOTE: This class is auto generated by the swagger code generator program (2.2.3).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package org.egov.works.qualitycontrol.web.controller;

import io.swagger.annotations.*;
import org.egov.works.qualitycontrol.web.contract.ErrorRes;
import org.egov.works.qualitycontrol.web.contract.QualityTestingRequest;
import org.egov.works.qualitycontrol.web.contract.QualityTestingResponse;
import org.egov.works.qualitycontrol.web.contract.RequestInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-17T10:32:55.512Z")

@Api(value = "qualitytestings", description = "the qualitytestings API")
public interface QualitytestingsApi {

    @ApiOperation(value = "Create new Quality Testing(s).", notes = "To create new Quality Testing in the system. API supports bulk creation with max limit as defined in the Quality Testing Request. Please note that either whole batch succeeds or fails, there's no partial batch success. To create one Quality Testing, please pass array with one Quality Testing object.  Quality Testing can be created after LOA/WO is approved. No workflow present for Quality Testing. ", response = QualityTestingResponse.class, tags={ "Quality Testing/Other Testing", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Quality Testing(s) created successfully", response = QualityTestingResponse.class),
        @ApiResponse(code = 400, message = "Quality Testing(s) creation failed", response = ErrorRes.class) })
    
    @RequestMapping(value = "/qualitytestings/_create",
        method = RequestMethod.POST)
    ResponseEntity<QualityTestingResponse> qualitytestingsCreatePost(@ApiParam(value = "Details of new Quality Testing(s) + RequestInfo meta data.", required = true) @Valid @RequestBody QualityTestingRequest qualityTestingRequest);


    @ApiOperation(value = "Get the list of Quality Testing(s) defined in the system.", notes = "Search and get Quality Testing(s) based on defined search criteria. Currently search parameters are only allowed as HTTP query params.  In case multiple parameters are passed Quality Testing(s) will be searched as an AND combination of all the parameters.  Maximum result size is restricted based on the maxlength of Quality Testing as defined in QualityTestingResponse model.  Search results will be sorted by the sortProperty Provided in the parameters ", response = QualityTestingResponse.class, tags={ "Quality Testing/Other Testing", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Quality Testing(s) Retrieved Successfully", response = QualityTestingResponse.class),
        @ApiResponse(code = 400, message = "Invalid input.", response = ErrorRes.class) })
    
    @RequestMapping(value = "/qualitytestings/_search",
        method = RequestMethod.POST)
    ResponseEntity<QualityTestingResponse> qualitytestingsSearchPost(@NotNull @ApiParam(value = "Unique id for a tenant.", required = true) @RequestParam(value = "tenantId", required = true) String tenantId, @ApiParam(value = "Parameter to carry Request metadata in the request body") @Valid @RequestBody RequestInfo requestInfo, @Min(0) @Max(100) @ApiParam(value = "Number of records returned.", defaultValue = "20") @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize, @ApiParam(value = "Page number", defaultValue = "1") @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber, @ApiParam(value = "This takes any field from the Object seperated by comma and asc,desc keywords. example name asc,code desc or name,code or name,code desc", defaultValue = "id") @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy, @Size(max = 50) @ApiParam(value = "Comma separated list of Work Order Numbers") @RequestParam(value = "workOrderNumbers", required = false) List<String> workOrderNumbers, @Size(max = 50) @ApiParam(value = "Work Order Numbers") @RequestParam(value = "workOrderNumberLike", required = false) String workOrderNumberLike, @Size(max = 50) @ApiParam(value = "Comma separated list of Ids of Quality Testing(s)") @RequestParam(value = "ids", required = false) List<String> ids, @Size(max = 50) @ApiParam(value = "Comma separated list of LOA Numbers") @RequestParam(value = "loaNumbers", required = false) List<String> loaNumbers, @Size(max = 50) @ApiParam(value = "LOA Number") @RequestParam(value = "loaNumberLike", required = false) String loaNumberLike, @Size(max = 50) @ApiParam(value = "Comma separated list of Detailed Estimate Numbers") @RequestParam(value = "detailedEstimateNumbers", required = false) List<String> detailedEstimateNumbers, @Size(max = 50) @ApiParam(value = "Detailed estimate Number") @RequestParam(value = "detailedEstimateNumberLike", required = false) String detailedEstimateNumberLike, @Size(max = 50) @ApiParam(value = "Comma separated list of Work Identification Numbers") @RequestParam(value = "workIdentificationNumbers", required = false) List<String> workIdentificationNumbers, @Size(max = 50) @ApiParam(value = "Work Identification Number") @RequestParam(value = "workIdentificationNumberLike", required = false) String workIdentificationNumberLike, @ApiParam(value = "Epoch time from when the Quality object created") @RequestParam(value = "fromDate", required = false) Long fromDate, @ApiParam(value = "Epoch time till the Quality object created") @RequestParam(value = "toDate", required = false) Long toDate);


    @ApiOperation(value = "Update existing Quality Testing(s).", notes = "To update existing Quality Testing in the system. API supports bulk updation with max limit as defined in the Quality Testing Request. Please note that either whole batch succeeds or fails, there's no partial batch success. To update one Quality Testing, please pass array with one Quality Testing object. ", response = QualityTestingResponse.class, tags={ "Quality Testing/Other Testing", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Quality Testing(s) updated successfully", response = QualityTestingResponse.class),
        @ApiResponse(code = 400, message = "Quality Testing(s) updation failed", response = ErrorRes.class) })
    
    @RequestMapping(value = "/qualitytestings/_update",
        method = RequestMethod.POST)
    ResponseEntity<QualityTestingResponse> qualitytestingsUpdatePost(@ApiParam(value = "Details of Quality Testing(s) + RequestInfo meta data.", required = true) @Valid @RequestBody QualityTestingRequest qualityTestingRequest);

}