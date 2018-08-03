package org.egov.lams.model;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.egov.lams.model.enums.Action;
import org.egov.lams.model.enums.BasisOfAllotment;
import org.egov.lams.model.enums.NatureOfAllotment;
import org.egov.lams.model.enums.PaymentCycle;
import org.egov.lams.model.enums.Source;
import org.egov.lams.model.enums.Status;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Agreement {

    private Long id;

    @NotNull
    private String tenantId;
    private String agreementNumber;
    private String acknowledgementNumber;
    private String stateId;

    @NotNull
    private Action action;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date agreementDate;

    @NotNull
    @Max(26)
    @Min(1)
    private Long timePeriod;

    @NotNull
    private Allottee allottee;

    @NotNull
    private Asset asset;

    private String tenderNumber;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date tenderDate;
    private String councilNumber;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date councilDate;

    @Min(0)
    private Double bankGuaranteeAmount;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date bankGuaranteeDate;

    @Min(0)
    private Double securityDeposit;

    @Min(0)
    private Double collectedSecurityDeposit;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date securityDepositDate;
    private Status status;

    private BasisOfAllotment basisOfAllotment;

    private NatureOfAllotment natureOfAllotment;
    @Min(0)
    private Double registrationFee;
    private String caseNo;

    @NotNull
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date commencementDate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date expiryDate;
    private String orderDetails;

    @NotNull
    @Min(0)
    private Double rent;
    private String tradelicenseNumber;

    @NotNull
    private PaymentCycle paymentCycle;
    private RentIncrementType rentIncrementMethod;
    private String orderNumber;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date orderDate;
    private String rrReadingNo;
    private String remarks;
    private String solvencyCertificateNo;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date solvencyCertificateDate;
    private String tinNumber;

    private List<Document> documents;
    private List<String> demands;
    private WorkflowDetails workflowDetails;

    @Min(0)
    private Double goodWillAmount;

    @Min(0)
    private Double collectedGoodWillAmount;

    @NotNull
    private Source source;
    private List<Demand> legacyDemands;
    private Cancellation cancellation;
    private Renewal renewal;
    private Eviction eviction;
    private Objection objection;
    private Judgement judgement;
    private Remission remission;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date createdDate;
    private String createdBy;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date lastmodifiedDate;
    private String lastmodifiedBy;
    private Boolean isAdvancePaid;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date adjustmentStartDate;
    private Boolean isUnderWorkflow;
    private String firstAllotment;

    private String gstin;

    private String municipalOrderNumber;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date municipalOrderDate;

    private String governmentOrderNumber;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date governmentOrderDate;
    private List<SubSeqRenewal> subSeqRenewals;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date renewalDate;

    private Long reservationCategory;

    private String oldAgreementNumber;
    private String referenceNumber;
    private String floorNumber;
    private Long parent;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date tenderOpeningDate;
    private Double auctionAmount;
    private Double solvencyAmount;
    private Boolean showDetails = Boolean.TRUE;
    private Double serviceTax;
    private Double cgst;
    private Double sgst;
    private String noticeNumber;
    private Boolean isHistory;

}
