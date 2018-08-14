package org.egov.collection.util;

import lombok.extern.slf4j.Slf4j;
import org.egov.collection.model.AuditDetails;
import org.egov.collection.model.Instrument;
import org.egov.collection.model.TransactionType;
import org.egov.collection.model.enums.CollectionType;
import org.egov.collection.model.enums.InstrumentTypesEnum;
import org.egov.collection.model.enums.ReceiptStatus;
import org.egov.collection.repository.BillingServiceRepository;
import org.egov.collection.repository.BusinessDetailsRepository;
import org.egov.collection.repository.IdGenRepository;
import org.egov.collection.repository.InstrumentRepository;
import org.egov.collection.web.contract.*;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.egov.collection.config.CollectionServiceConstants.*;

@Service
@Slf4j
public class ReceiptEnricher {
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private BillingServiceRepository billingRepository;
    private InstrumentRepository instrumentRepository;
    private IdGenRepository idGenRepository;
    private BusinessDetailsRepository businessDetailsRepository;

    @Autowired
    public ReceiptEnricher(BillingServiceRepository billingRepository, InstrumentRepository instrumentRepository,
                           IdGenRepository idGenRepository, BusinessDetailsRepository businessDetailsRepository) {
        this.billingRepository = billingRepository;
        this.instrumentRepository = instrumentRepository;
        this.idGenRepository = idGenRepository;
        this.businessDetailsRepository = businessDetailsRepository;
    }

    /**
     * Fetch instruments from financials for the given receipts
     *
     * @param requestInfo Request Info for the request
     * @param receipts Receipts to be enriched
     */
    public void enrichReceiptsWithInstruments(RequestInfo requestInfo, List<Receipt> receipts){
        Set<String> instruments = receipts.stream().map(receipt -> receipt.getInstrument().getId()).collect(Collectors
                .toSet());
        List<Instrument> fetchedInstruments = instrumentRepository.searchInstruments(String.join(",", instruments),
                requestInfo);

        Map<String, Instrument> map = fetchedInstruments.stream().collect(Collectors.toMap(Instrument::getId,
                instrument -> instrument));

        receipts.forEach(receipt -> receipt.setInstrument(map.get(receipt.getInstrument().getId())));
    }

    /**
     * Fetch bill from billing service for the provided bill id
     * Ensure bill exists and amount paid details exist for all bill details
     * Set paid by and amount paid for each bill detail in the new validated bill
     *
     * @param receiptReq Receipt to be enriched
     */
    public void enrichReceiptPreValidate(ReceiptReq receiptReq) {
        Receipt receipt = receiptReq.getReceipt().get(0);
        Bill billFromRequest = receipt.getBill().get(0);

        if(isNull(receiptReq.getRequestInfo().getUserInfo()) || isNull(receiptReq.getRequestInfo().getUserInfo()
                .getId())){
            throw new CustomException("USER_INFO_INVALID", "Invalid user info in request info, user id is mandatory");
        }

        List<Bill> validatedBills = billingRepository.fetchBill(receiptReq.getRequestInfo(), receipt.getTenantId(), billFromRequest.getId
                ());

        if (validatedBills.isEmpty() || validatedBills.get(0).getBillDetails().isEmpty()) {
            log.error("Bill ID provided does not exist or is in an invalid state " + billFromRequest.getId());
            throw new CustomException("INVALID_BILL_ID", "Bill ID provided does not exist or is in an invalid state");
        }

        if (validatedBills.get(0).getBillDetails().size() != billFromRequest.getBillDetails().size()) {
            log.error("Mismatch in bill details records provided in request and actual bill. Expected {} billdetails " +
                    "found {} in request", billFromRequest.getBillDetails().size(), validatedBills.get(0)
                    .getBillDetails().size());
            throw new CustomException("INVALID_BILL_ID", "Mismatch in bill detail records provided in request and actual bill");

        }

        Bill validatedBill = validatedBills.get(0);
        validatedBill.setPaidBy(billFromRequest.getPaidBy());

        for(int i = 0; i < validatedBill.getBillDetails().size(); i++){
            validatedBill.getBillDetails().get(i).setAmountPaid(billFromRequest.getBillDetails().get(i).getAmountPaid
                    ());

            if(receipt.getInstrument().getInstrumentType().getName().equalsIgnoreCase(InstrumentTypesEnum.ONLINE.name()))
                validatedBill.getBillDetails().get(i).setCollectionType(CollectionType.ONLINE);
            else
                validatedBill.getBillDetails().get(i).setCollectionType(CollectionType.COUNTER);

            if(Objects.isNull(validatedBill.getBillDetails().get(i).getReceiptDate()))
                validatedBill.getBillDetails().get(i).setReceiptDate(new Date().getTime());

            // Business service enrichment called in loop as they're always unique for a bill
            enrichBusinessService(receiptReq.getRequestInfo(), validatedBill.getBillDetails().get(i));
        }

        AuditDetails auditDetails = AuditDetails.builder().createdBy(receiptReq.getRequestInfo().getUserInfo().getId
                ()).createdDate(System.currentTimeMillis()).lastModifiedBy(receiptReq.getRequestInfo().getUserInfo().getId
                ()).lastModifiedDate(System.currentTimeMillis()).build();
        receipt.setBill(validatedBills);
        receipt.setAuditDetails(auditDetails);

    }


    /**
     * Fetches business details for given bill detail business service
     *
     * @param requestInfo Request Info of the request
     * @param billDetail Bill Detail for which business service to be fetched
     */
    private void enrichBusinessService(RequestInfo requestInfo, BillDetail billDetail) {
        BusinessDetailsResponse businessDetailsResponse = businessDetailsRepository.getBusinessDetails(Collections.singletonList(billDetail.getBusinessService()),
                billDetail.getTenantId(), requestInfo);

        if (isNull(businessDetailsResponse.getBusinessDetails()) || businessDetailsResponse
                .getBusinessDetails().isEmpty()) {
            log.error("Business detail not found for {} and tenant {}", billDetail.getBusinessService(), billDetail
                    .getTenantId());
            throw new CustomException(BUSINESSDETAILS_EXCEPTION_MSG, BUSINESSDETAILS_EXCEPTION_DESC);
        }
        else {
            billDetail.setReceiptType(businessDetailsResponse.getBusinessDetails().get(0).getBusinessType());
        }
    }


    /**
     * Enrich instrument for financials
     * For each bill detail,
     *  - Set status to approved by default for now, no workflow
     *  - Set collection type to online or counter
     *  - Set receipt date
     *  - Generate and set receipt number
     *
     * @param receiptReq Receipt request to be enriched
     */
    public void enrichReceiptPostValidate(ReceiptReq receiptReq){
        Receipt receipt = receiptReq.getReceipt().get(0);
        Bill bill = receipt.getBill().get(0);

        enrichInstrument(receiptReq);

        for (BillDetail billDetail : bill.getBillDetails()) {
            billDetail.setStatus(ReceiptStatus.APPROVED.toString());

            String receiptNumber = idGenRepository.generateReceiptNumber(receiptReq.getRequestInfo(), billDetail.getTenantId());
            billDetail.setReceiptNumber(receiptNumber);
        }

    }

    /**
     * Enrich the instrument object,
     *  - In case of cash / card [append card digits], generate transaction number
     *  - In case of online, dd, cheque use given txn number, and date
     *
     * @param receiptReq Receipt request to be enriched
     */
    private void enrichInstrument(ReceiptReq receiptReq){
        Receipt receipt = receiptReq.getReceipt().get(0);
        Instrument instrument = receipt.getInstrument();
        instrument.setTransactionType(TransactionType.Debit);
        instrument.setTenantId(receipt.getTenantId());
        instrument.setPayee(receipt.getBill().get(0).getPayeeName());

        try {

            if (instrument.getInstrumentType().getName().equalsIgnoreCase(InstrumentTypesEnum.CASH.name()) || instrument
                    .getInstrumentType().getName().equalsIgnoreCase(InstrumentTypesEnum.CARD.name())) {

                String transactionId = idGenRepository.generateTransactionNumber(receiptReq.getRequestInfo(),
                        receipt.getTenantId());
                String transactionDate = simpleDateFormat.format(new Date());
                instrument.setTransactionDate(simpleDateFormat.parse(transactionDate));

                if(instrument.getInstrumentType().getName().equalsIgnoreCase(InstrumentTypesEnum.CARD.name()))
                    instrument.setTransactionNumber(transactionId + "-"+instrument.getTransactionNumber());
                else
                    instrument.setTransactionNumber(transactionId);

            } else {
                DateTime transactionDate = new DateTime(instrument.getTransactionDateInput());
                instrument.setTransactionDate(simpleDateFormat.parse(transactionDate.toString("dd/MM/yyyy")));
            }

            receipt.setTransactionId(instrument.getTransactionNumber());

        }catch (ParseException ignored){}
    }
}
