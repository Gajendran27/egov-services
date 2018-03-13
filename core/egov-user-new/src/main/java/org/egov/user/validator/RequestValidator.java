package org.egov.user.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.response.ErrorField;
import org.egov.user.web.contract.User;
import org.egov.user.domain.service.UserService;
import org.egov.user.utils.UserConstants;
import org.egov.user.web.contract.UserRequest;
import org.egov.user.web.errorhandlers.Error;
import org.egov.user.web.errorhandlers.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RequestValidator {

	@Autowired
	private UserService userService;

	@Value("${user.login.password.otp.enabled}")
	private boolean isUserLoginPasswordOtpEnabled;

	public ErrorResponse populateErrors(final BindingResult errors) {
		final ErrorResponse errRes = new ErrorResponse();
		final Error error = new Error();
		error.setCode(1);
		error.setDescription("Error while binding request");
		List<ErrorField> errFields = new ArrayList<>();
		if (errors.hasFieldErrors()) {
			for (final FieldError fieldError : errors.getFieldErrors()) {
				ErrorField ef = new ErrorField();
				ef.setField(fieldError.getField());
				ef.setMessage("Value Entered : " + fieldError.getRejectedValue()
						+ " is not a valid value. Please re-enter !");
				errFields.add(ef);
				error.getFields().put(fieldError.getField(), fieldError.getRejectedValue());
				error.setMessage("Not matching the required expression :: ");
			}
			error.setErrorFields(errFields);
		}
		errRes.setError(error);
		return errRes;
	}

	public List<ErrorResponse> validateRequest(final UserRequest userRequest, Boolean createOrUpdate) {
		final List<ErrorResponse> errorResponses = new ArrayList<>();
		final ErrorResponse errorResponse = new ErrorResponse();
		final Error error = getError(userRequest, createOrUpdate);
		errorResponse.setError(error);
		if (!errorResponse.getErrorFields().isEmpty())
			errorResponses.add(errorResponse);
		log.info(errorResponses.size() + " Error Responses are found");
		return errorResponses;
	}

	private Error getError(final UserRequest userRequest, final Boolean createOrUpdate) {
		final List<ErrorField> errorFields = getErrorFields(userRequest, createOrUpdate);
		return Error.builder().code(HttpStatus.BAD_REQUEST.value()).message("Invalid User Request")
				.errorFields(errorFields).build();
	}

	private List<ErrorField> getErrorFields(final UserRequest userRequest, final Boolean createOrUpdate) {
		final List<ErrorField> errorFields = new ArrayList<>();
		for (final User user : userRequest.getUsers()) {
			if (!createOrUpdate)
				addUserIdValidationErrors(user.getId(), errorFields);
			addTenantIdValidationErrors(user.getTenantId(), errorFields);
			addUserNameValidationErrors(user.getUserName(), user.getTenantId(), errorFields, createOrUpdate);
			addNameValidationErrors(user.getName(), errorFields);
			addMobileNumberValidationErrors(user.getMobileNumber(), errorFields);
			addGenderValidationErrors(user.getGender(), errorFields);
			addLocaleValidationErrors(user.getLocale(), errorFields);
			addTypeValidationErrors(user.getType(), errorFields);
			addActiveValidationErrors(user.getActive(), errorFields);
			if (null != user.getUserDetails()) {
/*				List<Address> addresses = user.getUserDetails().getAddresses();
				if (null != addresses && !addresses.isEmpty()) {
					for (Address address : addresses) {
						addUserAddressValidationErrors(address, errorFields);
					}

				}*/
			}
		}
		return errorFields;
	}

/*	private void addUserAddressValidationErrors(Address address, List<ErrorField> errorFields) {
		if (null != address.getAddress() && address.getAddress().length() > 300) {
			final ErrorField errorField = ErrorField.builder().code(UserConstants.ADDRESS_INVALIDLENGTH_CODE)
					.message(UserConstants.ADDRESS_INVALIDFORMAT_ERROR_MESSAGE)
					.field(UserConstants.ADDRESSE_INVALIDFORMAT_NAME).build();
			errorFields.add(errorField);
		}
		if (null != address.getCity() && address.getCity().length() > 300) {
			final ErrorField errorField = ErrorField.builder().code(UserConstants.CITY_INVALIDLENGTH_CODE)
					.message(UserConstants.CITY_INVALIDFORMAT_ERROR_MESSAGE)
					.field(UserConstants.CITY_INVALIDFORMAT_NAME).build();
			errorFields.add(errorField);
		}
		if (null != address.getPinCode() && address.getPinCode().length() > 10) {
			final ErrorField errorField = ErrorField.builder().code(UserConstants.PINCODE_INVALIDLENGTH_CODE)
					.message(UserConstants.PINCODE_INVALIDFORMAT_ERROR_MESSAGE)
					.field(UserConstants.PINCODE_INVALIDFORMAT_NAME).build();
			errorFields.add(errorField);
		}

	}*/

	private void addUserIdValidationErrors(Long id, List<ErrorField> errorFields) {
		// TODO Auto-generated method stub
		if (null == id) {
			final ErrorField errorField = ErrorField.builder().code(UserConstants.USERID_MANDATORY_CODE)
					.message(UserConstants.USERID_MANADATORY_ERROR_MESSAGE)
					.field(UserConstants.USERID_MANADATORY_FIELD_NAME).build();
			errorFields.add(errorField);
		} else
			return;
	}

	private void addTypeValidationErrors(String type, List<ErrorField> errorFields) {
		// TODO Auto-generated method stub
		if (StringUtils.isBlank(type)) {
			final ErrorField errorField = ErrorField.builder().code(UserConstants.TYPE_MANDATORY_CODE)
					.message(UserConstants.TYPE_MANADATORY_ERROR_MESSAGE)
					.field(UserConstants.TYPE_MANADATORY_FIELD_NAME).build();
			errorFields.add(errorField);
		} else
			return;

	}

	private void addLocaleValidationErrors(String locale, List<ErrorField> errorFields) {
		// TODO Auto-generated method stub
		if (StringUtils.isBlank(locale)) {
			final ErrorField errorField = ErrorField.builder().code(UserConstants.LOCALE_MANDATORY_CODE)
					.message(UserConstants.LOCALE_MANADATORY_ERROR_MESSAGE)
					.field(UserConstants.LOCALE_MANADATORY_FIELD_NAME).build();
			errorFields.add(errorField);
		} else
			return;

	}

	private void addGenderValidationErrors(String gender, List<ErrorField> errorFields) {
		// TODO Auto-generated method stub
		if (StringUtils.isBlank(gender)) {
			final ErrorField errorField = ErrorField.builder().code(UserConstants.GENDER_MANDATORY_CODE)
					.message(UserConstants.GENDER_MANADATORY_ERROR_MESSAGE)
					.field(UserConstants.GENDER_MANADATORY_FIELD_NAME).build();
			errorFields.add(errorField);
		} else
			return;
	}

	private void addMobileNumberValidationErrors(String mobileNumber, List<ErrorField> errorFields) {
		// TODO Auto-generated method stub
		if (StringUtils.isBlank(mobileNumber)) {
			final ErrorField errorField = ErrorField.builder().code(UserConstants.MOBILENUMBER_MANDATORY_CODE)
					.message(UserConstants.MOBILENUMBER_MANADATORY_ERROR_MESSAGE)
					.field(UserConstants.MOBILENUMBER_MANADATORY_FIELD_NAME).build();
			errorFields.add(errorField);
		} else
			return;

	}

	private void addNameValidationErrors(String name, List<ErrorField> errorFields) {
		// TODO Auto-generated method stub
		if (StringUtils.isBlank(name)) {
			final ErrorField errorField = ErrorField.builder().code(UserConstants.NAME_MANDATORY_CODE)
					.message(UserConstants.NAME_MANADATORY_ERROR_MESSAGE)
					.field(UserConstants.NAME_MANADATORY_FIELD_NAME).build();
			errorFields.add(errorField);
		} else
			return;
	}

	private void addUserNameValidationErrors(String userName, String tenantId, List<ErrorField> errorFields,
			boolean createOrUpdate) {
		// TODO Auto-generated method stub
		if (StringUtils.isBlank(userName)) {
			final ErrorField errorField = ErrorField.builder().code(UserConstants.USERNAME_MANDATORY_CODE)
					.message(UserConstants.USERNAME_MANADATORY_ERROR_MESSAGE)
					.field(UserConstants.USERNAME_MANADATORY_FIELD_NAME).build();
			errorFields.add(errorField);
		} else if (isUserLoginPasswordOtpEnabled) {
			if (!StringUtils.isNumeric(userName)) {
				final ErrorField errorField = ErrorField.builder().code(UserConstants.USERNAME_INVALIDFORMAT_CODE)
						.message(UserConstants.USERNAME_INVALIDFORMAT_ERROR_MESSAGE)
						.field(UserConstants.USERNAME_INVALIDFORMAT_NAME).build();
				errorFields.add(errorField);
			}
		}

		else if (createOrUpdate) {
			if (userService.isUserPresent(userName, tenantId)) {
				final ErrorField errorField = ErrorField.builder().code(UserConstants.USERNAME_UNIQUE_CODE)
						.message(UserConstants.USERNAME_UNQ_ERROR_MESSAGE).field(UserConstants.USERNAME_UNQ_FIELD_NAME)
						.build();
				errorFields.add(errorField);
			}
		} else {
			if (!userService.isUserPresent(userName, tenantId)) {
				final ErrorField errorField = ErrorField.builder().code(UserConstants.USERNAME_DUPLICATE_CODE)
						.message(UserConstants.USERNAME_DUPLICATE_ERROR_MESSAGE)
						.field(UserConstants.USERNAME_DUPLICATE_FIELD_NAME).build();
				errorFields.add(errorField);
			}
		}
		return;
	}

	private void addTenantIdValidationErrors(final String tenantId, final List<ErrorField> errorFields) {

		if (StringUtils.isBlank(tenantId)) {
			final ErrorField errorField = ErrorField.builder().code(UserConstants.TENANTID_MANDATORY_CODE)
					.message(UserConstants.TENANTID_MANADATORY_ERROR_MESSAGE)
					.field(UserConstants.TENANTID_MANADATORY_FIELD_NAME).build();
			errorFields.add(errorField);
		} else
			return;
	}

	private void addActiveValidationErrors(final Boolean active, final List<ErrorField> errorFields) {

		if (null == active) {
			final ErrorField errorField = ErrorField.builder().code(UserConstants.ACTIVE_MANDATORY_CODE)
					.message(UserConstants.ACTIVE_MANADATORY_ERROR_MESSAGE)
					.field(UserConstants.ACTIVE_MANADATORY_FIELD_NAME).build();
			errorFields.add(errorField);
		} else
			return;
	}

}
