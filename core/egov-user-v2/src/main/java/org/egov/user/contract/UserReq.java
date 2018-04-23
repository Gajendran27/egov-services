package org.egov.user.contract;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class UserReq   {
  @JsonProperty("RequestInfo")
  private RequestInfo requestInfo;

  @JsonProperty("Users")
  @Valid
  private List<User> users;
}

