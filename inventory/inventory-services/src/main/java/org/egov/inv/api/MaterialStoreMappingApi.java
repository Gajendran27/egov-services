/**
 * NOTE: This class is auto generated by the swagger code generator program (2.3.0-SNAPSHOT).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package org.egov.inv.api;

import io.swagger.annotations.*;
import org.egov.inv.model.ErrorRes;
import org.egov.inv.model.MaterialStoreMappingRequest;
import org.egov.inv.model.MaterialStoreMappingResponse;
import org.egov.inv.model.RequestInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-11-02T16:27:56.269+05:30")

@Api(value = "materialstoremapping", description = "the materialstoremapping API")
public interface MaterialStoreMappingApi {

    @ApiOperation(value = "Create  new  material store mappings", nickname = "materialstoremappingCreatePost", notes = "Create  new  material store mappings", response = MaterialStoreMappingResponse.class, tags = {"MaterialStoreMappings",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Material Store Mapping created Successfully", response = MaterialStoreMappingResponse.class),
            @ApiResponse(code = 400, message = "Invalid Input", response = ErrorRes.class)})
    @RequestMapping(value = "/materialstoremapping/_create",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.POST)
    ResponseEntity<MaterialStoreMappingResponse> materialstoremappingCreatePost(@NotNull @ApiParam(value = "Unique id for a tenant.", required = true) @Valid @RequestParam(value = "tenantId", required = true) String tenantId, @ApiParam(value = "Create  new") @Valid @RequestBody MaterialStoreMappingRequest materialStoreMappingRequest, @RequestHeader(value = "Accept", required = false) String accept, BindingResult errors) throws Exception;


    @ApiOperation(value = "Update any of the materials", nickname = "materialstoremappingUpdatePost", notes = "Update any of the materials", response = MaterialStoreMappingResponse.class, tags = {"Material",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Material store mapping updated Successfully", response = MaterialStoreMappingResponse.class),
            @ApiResponse(code = 400, message = "Invalid Input", response = ErrorRes.class)})
    @RequestMapping(value = "/materialstoremapping/_update",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.POST)
    ResponseEntity<MaterialStoreMappingResponse> materialstoremappingUpdatePost(@NotNull @ApiParam(value = "Unique id for a tenant.", required = true) @Valid @RequestParam(value = "tenantId", required = true) String tenantId, @ApiParam(value = "common Request info") @Valid @RequestBody MaterialStoreMappingRequest materialStoreMappingRequest, @RequestHeader(value = "Accept", required = false) String accept, BindingResult errors) throws Exception;

    @ApiOperation(value = "Get the list of material store mappings", notes = "material store mappings", response = MaterialStoreMappingResponse.class, tags = {"MaterialStoreMappings",})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Material Store Mapping retrieved Successfully", response = MaterialStoreMappingResponse.class),
            @ApiResponse(code = 400, message = "Invalid Input", response = ErrorRes.class)})

    @RequestMapping(value = "/materialstoremapping/_search",
            produces = {"application/json"},
            consumes = {"application/json"},
            method = RequestMethod.POST)
    ResponseEntity<MaterialStoreMappingResponse> materialstoremappingSearchPost(@NotNull @ApiParam(value = "Unique id for a tenant.", required = true) @RequestParam(value = "tenantId", required = true) String tenantId, @ApiParam(value = "Parameter to carry Request metadata in the request body") @Valid @RequestBody RequestInfo requestInfo, @Size(max = 50) @ApiParam(value = "comma seperated list of Ids") @RequestParam(value = "ids", required = false) List<String> ids, @ApiParam(value = "material code of material store mapping ") @RequestParam(value = "material", required = false) String material, @ApiParam(value = "store code of material store mapping ") @RequestParam(value = "store", required = false) String store, @ApiParam(value = "active flag of material store mapping ") @RequestParam(value = "active", required = false) Boolean active, @Min(0) @Max(100) @ApiParam(value = "Number of records returned.", defaultValue = "20") @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize, @ApiParam(value = "offset") @RequestParam(value = "offset", required = false) Integer offset, @ApiParam(value = "Page number", defaultValue = "1") @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber, @ApiParam(value = "This takes any field from the Object seperated by comma and asc,desc keywords. example name asc,code desc or name,code or name,code desc", defaultValue = "id") @RequestParam(value = "sortBy", required = false, defaultValue = "id") String sortBy, @RequestHeader(value = "Accept", required = false) String accept, BindingResult errors) throws Exception;


}
