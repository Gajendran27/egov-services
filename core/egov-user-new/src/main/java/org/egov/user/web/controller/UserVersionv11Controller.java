package org.egov.user.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.user.domain.service.UserServiceVersionv11;
import org.egov.user.domain.v11.model.User;
import org.egov.user.domain.v11.model.UserSearchCriteria;
import org.egov.user.utils.UserUtil;
import org.egov.user.validator.RequestValidator;
import org.egov.user.web.errorhandlers.ErrorResponse;
import org.egov.user.web.v11.contract.UserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v110")
public class UserVersionv11Controller {

	@Autowired
	private UserServiceVersionv11 userService;

	@Autowired
	RequestValidator requestValidator;

	@Autowired
	UserUtil userUtil;

   /**
    * This Api Will create Bulk Users
    * @param userRequest
    * @param errors
    * @return
    */
	@PostMapping(value = "/_create")
	@ResponseBody
	public ResponseEntity<?> create(@RequestBody @Valid final UserRequest userRequest, final BindingResult errors) {
		if (errors.hasErrors()) {
			final List<ErrorResponse> errResList = new ArrayList<>();
			errResList.add(requestValidator.populateErrors(errors));

			return new ResponseEntity<>(errResList, HttpStatus.BAD_REQUEST);
		}
		final List<ErrorResponse> errorResponses = requestValidator.validateRequest(userRequest, Boolean.TRUE);
		if (!errorResponses.isEmpty())
			return new ResponseEntity<>(errorResponses, HttpStatus.BAD_REQUEST);
		userService.createUser(userRequest);
		return userUtil.createSuccessResponse(userRequest.getRequestInfo(), userRequest.getUsers());
	}

	/**
	 * Method will update the bulk users
	 * @param userRequest
	 * @param errors
	 * @return
	 */
	@PostMapping("/_update")
	public ResponseEntity<?> updateUser(@RequestBody @Valid final UserRequest userRequest, final BindingResult errors) {
		if (errors.hasErrors()) {
			final List<ErrorResponse> errResList = new ArrayList<>();
			errResList.add(requestValidator.populateErrors(errors));

			return new ResponseEntity<>(errResList, HttpStatus.BAD_REQUEST);
		}
		final List<ErrorResponse> errorResponses = requestValidator.validateRequest(userRequest, Boolean.FALSE);
		if (!errorResponses.isEmpty())
			return new ResponseEntity<>(errorResponses, HttpStatus.BAD_REQUEST);
		userService.updateUser(userRequest);
		return userUtil.createSuccessResponse(userRequest.getRequestInfo(), userRequest.getUsers());
	}

	/**
	 * This function Will use to search the users based on criteria
	 * @param requestInfo
	 * @param tenantId
	 * @param id
	 * @param name
	 * @param userName
	 * @param mobileNumber
	 * @param aadharNumber
	 * @param emailId
	 * @param pan
	 * @param active
	 * @param type
	 * @param roleCodes
	 * @param lastChangedSince
	 * @param includeDetails
	 * @param pageSize
	 * @param pageNumber
	 * @param sort
	 * @return
	 */
	@PostMapping("/_search")
	public ResponseEntity<?> searchUsers(@RequestBody RequestInfo requestInfo,
			@RequestParam(value = "tenantId", required = true) String tenantId,
			@RequestParam(value = "id", required = false) List<Long> id,
			@RequestParam(value = "name", required = false) String name,
			@RequestParam(value = "userName", required = false) String userName,
			@RequestParam(value = "mobileNumber", required = false) String mobileNumber,
			@RequestParam(value = "aadharNumber", required = false) String aadharNumber,
			@RequestParam(value = "emailId", required = false) String emailId,
			@RequestParam(value = "pan", required = false) String pan,
			@RequestParam(value = "active", required = false) boolean active,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "roleCodes", required = false) List<String> roleCodes,
			@RequestParam(value = "lastChangedSince", required = false) Long lastChangedSince,
			@RequestParam(value = "includeDetails", required = false) boolean includeDetails,
			@RequestParam(value = "pageSize", required = false) Integer pageSize,
			@RequestParam(value = "pageNumber", required = false) Integer pageNumber,
			@RequestParam(value = "sort", required = false) String sort) {

		UserSearchCriteria searchCriteria = UserSearchCriteria.builder().tenantId(tenantId).id(id).name(name)
				.userName(userName).aadhaarNumber(aadharNumber).mobileNumber(mobileNumber).emailId(emailId).pan(pan)
				.active(active).type(type).roleCodes(roleCodes).lastChangedSince(lastChangedSince)
				.includeDetails(includeDetails).build();

		List<User> users = userService.searchUsers(requestInfo, searchCriteria);
		return userUtil.createSuccessResponse(requestInfo, users);
	}

	
	/**
	 * This function will create opt will send to mobileNumber
	 * @param requestInfo
	 * @param tenantId
	 * @param userName
	 * @param mobileNumber
	 * @return
	 */
	@PostMapping("/_sendotp")
	public ResponseEntity<?> sendOtp(@RequestBody RequestInfo requestInfo,
			@RequestParam(value = "tenantId", required = true) String tenantId,
			@RequestParam(value = "userName", required = true) String userName,
			@RequestParam(value = "identity", required = true) String mobileNumber) {
		userService.createAndSendOtp(requestInfo, tenantId, userName, mobileNumber);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
