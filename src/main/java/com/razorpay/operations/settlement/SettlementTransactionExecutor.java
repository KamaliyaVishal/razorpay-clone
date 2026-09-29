package com.razorpay.operations.settlement;

import com.razorpay.common.dto.SettlementBankDetails;
import com.razorpay.common.entity.Money;
import com.razorpay.common.enums.EventAggregateType;
import com.razorpay.common.enums.SettlementStatus;
import com.razorpay.common.exception.ResourceNotFoundException;
import com.razorpay.merchant.api.MerchantLookupService;
import com.razorpay.operations.entity.Settlement;
import com.razorpay.operations.entity.SettlementPayment;
import com.razorpay.operations.entity.SettlementPaymentId;
import com.razorpay.operations.repository.SettlementPaymentRepository;
import com.razorpay.operations.repository.SettlementRepository;
import com.razorpay.operations.settlement.dto.BankTransferResult;
import com.razorpay.payment.api.PaymentLookupService;
import com.razorpay.payment.entity.Payment;
import com.razorpay.payment.outbox.OutboxEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SettlementTransactionExecutor {

    private static final double FEE_RATE = 0.02;
    private static final double GST_RATE = 0.18;

    private final PaymentLookupService paymentLookupService;
    private final SettlementRepository settlementRepository;
    private final SettlementPaymentRepository settlementPaymentRepository;
    private final MerchantLookupService merchantLookupService;
    private final BankTransferProcessor bankTransferProcessor;
    // Todo: publisher inside it's own db
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional(rollbackFor = Exception.class)
    public void processForMerchant(UUID merchantId, LocalDate settlementDate) {
        List<Payment> unsettledPayments = paymentLookupService.findUnsettledCapturedPayments(merchantId);
        if (unsettledPayments.isEmpty()) return;

        log.info("Processing {} unsettled payments for merchantId: {} on {} date",
                unsettledPayments.size(), merchantId, settlementDate);

        Money gross = unsettledPayments.stream()
                .map(Payment::getAmount)
                .reduce(Money::add)
                .orElseThrow();

        int fee = Math.toIntExact(Math.round(gross.getAmountUnits() * FEE_RATE));
        int gst = Math.toIntExact(Math.round(fee * GST_RATE));
        Money feeAmount = Money.of(fee, gross.getCurrency());
        Money gstAmount = Money.of(gst, gross.getCurrency());
        Money netAmount = gross.subtract(feeAmount).subtract(gstAmount);

        Settlement settlement = Settlement.builder()
                .merchantId(merchantId)
                .grossAmount(gross)
                .feeAmount(feeAmount)
                .taxAmount(gstAmount)
                .netAmount(netAmount)
                .status(SettlementStatus.INITIATED)
                .build();

        settlementRepository.save(settlement);

        try {
            List<SettlementPayment> links = new ArrayList<>();
            for (Payment p : unsettledPayments) {
                links.add(SettlementPayment.builder()
                        .id(new SettlementPaymentId(settlement.getId(), p.getId()))
                        .settlement(settlement)
                        .build());
            }
            settlementPaymentRepository.saveAll(links);

            SettlementBankDetails settlementBankDetails = merchantLookupService.getSettlementBankDetails(merchantId);
            BankTransferResult bankTransferResult = bankTransferProcessor.initiate(settlement.getId(), merchantId, netAmount,
                    settlementBankDetails.accountNumber(), settlementBankDetails.ifsc());

            settlement.setStatus(SettlementStatus.TRANSFER_PENDING);
            settlement.setBankReference(bankTransferResult.registrationRef());

            settlementRepository.save(settlement);
        } catch (Exception e) {
            log.error("Settlement failed for settlementId: {} on date: {}", settlement.getId(), settlementDate, e);
            settlement.setStatus(SettlementStatus.FAILED);
            settlementRepository.save(settlement);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void resolveTransfer(UUID settlementId,
                                String errorCode, String errorDescription) {

        Settlement settlement = settlementRepository.findById(settlementId).orElseThrow(
                () -> new ResourceNotFoundException("Settlement", settlementId));

        if (settlement.getStatus() != SettlementStatus.TRANSFER_PENDING) {
            log.info("Settlement resolved, skipping for id: {}", settlement.getId());
            return;
        }

        if (errorCode == null) {
            // case: Success
            settlement.setStatus(SettlementStatus.PROCESSED);
            settlement.setProcessedAt(LocalDateTime.now());
            settlementRepository.save(settlement);
            log.info("Settlement processed successfully, settlementId: {}", settlement.getId());

            outboxEventPublisher.publish(
                    EventAggregateType.SETTLEMENT,
                    settlementId,
                    "SETTLEMENT_PROCESSED", Map.of(
                            "settlementId", settlement,
                            "merchantId", settlement.getMerchantId(),
                            "status", settlement.getStatus().name(),
                            "settlementAmount", settlement.getNetAmount().getAmountUnits(),
                            "settlementCurrency", settlement.getNetAmount().getCurrency()
                    ));
        } else {
            // Case: Failed
            settlement.setStatus(SettlementStatus.FAILED);
            settlement.setFailureReason(errorCode + " : " + errorDescription);
            settlementRepository.save(settlement);
            log.warn("Settlement failed, settlementId: {}", settlement.getId());

            outboxEventPublisher.publish(
                    EventAggregateType.SETTLEMENT,
                    settlementId,
                    "SETTLEMENT_FAILED", Map.of(
                            "settlementId", settlement,
                            "merchantId", settlement.getMerchantId(),
                            "status", settlement.getStatus().name(),
                            "settlementAmount", settlement.getNetAmount().getAmountUnits(),
                            "settlementCurrency", settlement.getNetAmount().getCurrency()
                    ));
        }

    }
}




















