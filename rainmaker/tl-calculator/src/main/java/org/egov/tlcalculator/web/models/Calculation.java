package org.egov.tlcalculator.web.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * Calculation
 */
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-09-27T14:56:03.454+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Calculation   {
        @JsonProperty("applicationNumber")
        
private String applicationNumber = null;

        @JsonProperty("totalAmount")
        
private Double totalAmount = null;

        @JsonProperty("penalty")
        
private Double penalty = null;

        @JsonProperty("exemption")
        
private Double exemption = null;

        @JsonProperty("rebate")
        
private Double rebate = null;

        @JsonProperty("tenantId")
        @Size(min=2,max=256) 
private String tenantId = null;


}

