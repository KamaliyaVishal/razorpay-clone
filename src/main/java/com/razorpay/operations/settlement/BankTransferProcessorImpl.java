package com.razorpay.operations.settlement;

import com.razorpay.common.entity.Money;
import com.razorpay.common.util.RandomizerUtil;
import com.razorpay.operations.settlement.dto.BankTransferResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class BankTransferProcessorImpl implements BankTransferProcessor {

    @Value("${app.operation.randomToken-length:12}")
    private Integer tokenLength;

    @Override
    public BankTransferResult initiate(UUID settlementId, UUID merchantId, Money amount, String bankAccount, String ifsc) {

        // Call the Bank API
        String registrationRef = "TXN_" + RandomizerUtil.randomBase64(tokenLength);

        log.debug("Bank Transfer call completed for settlementId: {}, registrationRef: {}",
                settlementId, registrationRef);

        return new BankTransferResult(registrationRef);
    }
}
