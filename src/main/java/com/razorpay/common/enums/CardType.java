package com.razorpay.common.enums;

import lombok.Getter;

import java.util.regex.Pattern;

@Getter
public enum CardType {

    // Starts with 4
    VISA(Pattern.compile("^4[0-9]{12}(?:[0-9]{3})?$")),

    // Starts with 51-55 or modern 2221-2720 ranges
    MASTERCARD(Pattern.compile("^(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}$")),

    // Starts with 34 or 37
    AMERICAN_EXPRESS(Pattern.compile("^3[47][0-9]{13}$")),

    // Discover starts with 6011, 622126-622925, 644-649, 65
    DISCOVER(Pattern.compile("^6(?:011|5[0-9]{2})[0-9]{12}$")),

    // JCB starts with 3528-3589
    JCB(Pattern.compile("^35(?:2[89]|[3-8][0-9])[0-9]{12}$")),

    // RuPay starts with 60, 65, 81, 82, 508 (Commonly co-branded with Discover/Maestro)
    RUPAY(Pattern.compile("^6[05][0-9]{14}|8[12][0-9]{14}$")),

    UNKNOWN(null);

    private final Pattern pattern;

    CardType(Pattern pattern) {
        this.pattern = pattern;
    }

}
