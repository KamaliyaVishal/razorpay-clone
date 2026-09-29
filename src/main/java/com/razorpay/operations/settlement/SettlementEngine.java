package com.razorpay.operations.settlement;

import com.razorpay.merchant.api.MerchantLookupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
@Slf4j
@RequiredArgsConstructor
public class SettlementEngine {

    private final MerchantLookupService merchantLookupService;
    private final SettlementTransactionExecutor settlementTransactionExecutor;

    @Scheduled(cron = "0 0 23 * * *")
    public void runScheduled() {
        log.info("Nightly settlement running..");
        run();
    }

    public void run() {
        List<UUID> merchantIds = merchantLookupService.listActiveMerchantIds();
        log.info("Processing the settlement for {} merchantIds", merchantIds.size());

        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {

            List<Future<?>> futures = new ArrayList<>();
            for (UUID merchantId : merchantIds) {
                futures.add(executorService.submit(() -> {
                    settlementTransactionExecutor.processForMerchant(merchantId, LocalDate.now());
                }));
            }

            for (Future<?> f : futures) {
                try {
                    f.get();
                } catch (InterruptedException | ExecutionException e) {
                    log.error("Settlement batch future failed ", e);
                    throw new RuntimeException(e);
                }
            }
        }
        log.info("Settlement batch completed");
    }

}


















