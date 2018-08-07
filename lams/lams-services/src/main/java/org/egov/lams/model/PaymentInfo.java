package org.egov.lams.model;

import java.math.BigDecimal;
import java.util.Date;

public class PaymentInfo {
	private String receiptNumber;
	private BigDecimal receiptAmount;
	private Date receiptDate;
	private String status;
	private String description;
	private String taxReason;
	private String glCode;
	private Double creditedAmount;
	private Double creditAmountToBePaid;
	private Double debitedAmount;
	private String taxPeriod;

	public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	public BigDecimal getReceiptAmount() {
		return receiptAmount;
	}

	public void setReceiptAmount(BigDecimal receiptAmount) {
		this.receiptAmount = receiptAmount;
	}

	public Date getReceiptDate() {
		return receiptDate;
	}

	public String getDescription() {
		return description;
	}

	public String getTaxReason() {
		return taxReason;
	}

	public void setTaxReason(String taxReason) {
		this.taxReason = taxReason;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setReceiptDate(Date receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGlCode() {
		return glCode;
	}

	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}

	public Double getCreditedAmount() {
		return creditedAmount;
	}

	public void setCreditedAmount(Double creditedAmount) {
		this.creditedAmount = creditedAmount;
	}

	public Double getCreditAmountToBePaid() {
		return creditAmountToBePaid;
	}

	public void setCreditAmountToBePaid(Double creditAmountToBePaid) {
		this.creditAmountToBePaid = creditAmountToBePaid;
	}

	public Double getDebitedAmount() {
		return debitedAmount;
	}

	public void setDebitedAmount(Double debitedAmount) {
		this.debitedAmount = debitedAmount;
	}

	public String getTaxPeriod() {
		return taxPeriod;
	}

	public void setTaxPeriod(String taxPeriod) {
		this.taxPeriod = taxPeriod;
	}
}
