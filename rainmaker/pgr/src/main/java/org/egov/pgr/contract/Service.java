package org.egov.pgr.contract;

import java.math.BigDecimal;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.egov.pgr.contract.AuditDetails.AuditDetailsBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Defines the structure of a service provided by the administration. This is based on Open311 standard, but extends it in follwoing important ways -  1. metadata is changed from boolean to strign and represents a valid swgger 2.0 definition url of the metadata definition. If this is null then it is assumed taht service does not have any metadata, else the metadata is defined in the OpenAPI definition. This allows for a well structured powerful metadata definition.  2. Due to this ServiceRequest object has been enhanced to include metadata values (aka attribute value in Open311) as an JSON object. 
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-23T09:30:28.401Z")


@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Service   {
  @JsonProperty("tenantId")
  private String tenantId = null;

  @JsonProperty("serviceCode")
  private String serviceCode = null;

  @JsonProperty("serviceName")
  private String serviceName = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("metadata")
  private String metadata = null;

  /**
   * realtime (Currently we only support real time srevices)
   */
  public enum TypeEnum {
    REALTIME("realtime"),
    
    BATCH("batch"),
    
    BLACKBOX("blackbox");

    private String value;

    TypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static TypeEnum fromValue(String text) {
      for (TypeEnum b : TypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("type")
  private TypeEnum type = null;

  @JsonProperty("keywords")
  private String keywords = null;

  @JsonProperty("group")
  private String group = null;

  @JsonProperty("slaHours")
  private BigDecimal slaHours = null;

  public Service tenantId(String tenantId) {
    this.tenantId = tenantId;
    return this;
  }

   /**
   * The unique identifier for Service - this is equivalent to jurisdiction_id in Open311. As the platform intends to be multi tenanted - this is always required
   * @return tenantId
  **/
  @NotNull


  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  public Service serviceCode(String serviceCode) {
    this.serviceCode = serviceCode;
    return this;
  }

   /**
   * The unique identifier for Service
   * @return serviceCode
  **/
  @NotNull


  public String getServiceCode() {
    return serviceCode;
  }

  public void setServiceCode(String serviceCode) {
    this.serviceCode = serviceCode;
  }

  public Service serviceName(String serviceName) {
    this.serviceName = serviceName;
    return this;
  }

   /**
   * Service name.
   * @return serviceName
  **/
  @NotNull


  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public Service description(String description) {
    this.description = description;
    return this;
  }

   /**
   * Services description.
   * @return description
  **/

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Service metadata(String metadata) {
    this.metadata = metadata;
    return this;
  }

   /**
   * Schema url to valid OpenAPI swagger difinition. As described in the definition above these are the additional service specific attributes defined as a swagger definition
   * @return metadata
  **/

  public String getMetadata() {
    return metadata;
  }

  public void setMetadata(String metadata) {
    this.metadata = metadata;
  }

  public Service type(TypeEnum type) {
    this.type = type;
    return this;
  }

   /**
   * realtime (Currently we only support real time srevices)
   * @return type
  **/


  public TypeEnum getType() {
    return type;
  }

  public void setType(TypeEnum type) {
    this.type = type;
  }

  public Service keywords(String keywords) {
    this.keywords = keywords;
    return this;
  }

   /**
   * tags for this service.
   * @return keywords
  **/


  public String getKeywords() {
    return keywords;
  }

  public void setKeywords(String keywords) {
    this.keywords = keywords;
  }

  public Service group(String group) {
    this.group = group;
    return this;
  }

   /**
   * Group associated to service.
   * @return group
  **/


  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public Service slaHours(BigDecimal slaHours) {
    this.slaHours = slaHours;
    return this;
  }

   /**
   * Service Level Agreement in hours for Service.
   * @return slaHours
  **/

  @Valid

  public BigDecimal getSlaHours() {
    return slaHours;
  }

  public void setSlaHours(BigDecimal slaHours) {
    this.slaHours = slaHours;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Service service = (Service) o;
    return Objects.equals(this.tenantId, service.tenantId) &&
        Objects.equals(this.serviceCode, service.serviceCode) &&
        Objects.equals(this.serviceName, service.serviceName) &&
        Objects.equals(this.description, service.description) &&
        Objects.equals(this.metadata, service.metadata) &&
        Objects.equals(this.type, service.type) &&
        Objects.equals(this.keywords, service.keywords) &&
        Objects.equals(this.group, service.group) &&
        Objects.equals(this.slaHours, service.slaHours);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tenantId, serviceCode, serviceName, description, metadata, type, keywords, group, slaHours);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Service {\n");
    
    sb.append("    tenantId: ").append(toIndentedString(tenantId)).append("\n");
    sb.append("    serviceCode: ").append(toIndentedString(serviceCode)).append("\n");
    sb.append("    serviceName: ").append(toIndentedString(serviceName)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    metadata: ").append(toIndentedString(metadata)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    keywords: ").append(toIndentedString(keywords)).append("\n");
    sb.append("    group: ").append(toIndentedString(group)).append("\n");
    sb.append("    slaHours: ").append(toIndentedString(slaHours)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

