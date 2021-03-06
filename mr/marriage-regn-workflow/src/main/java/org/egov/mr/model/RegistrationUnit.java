package org.egov.mr.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@Builder
public class RegistrationUnit {

	private Long id;

	@NotNull
	@Size(min = 1, max = 256)
	private String code;

	@NotNull
	@Size(min = 1, max = 256)
	private String name;

	private Location address;

	private Boolean isActive;

	@NotNull
	@Size(min = 1, max = 256)
	private String tenantId;

	private AuditDetails auditDetails;
}
