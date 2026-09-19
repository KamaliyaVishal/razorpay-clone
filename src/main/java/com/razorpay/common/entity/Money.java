package com.razorpay.common.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Money {

    private int amountUnit; // Amount in the smallest currency unit (e.g., cents for USD)
    private String currency; // Currency code (e.g., USD, EUR)

    public static Money of(int amountUnit, String currency) {
        return new Money(amountUnit, currency);
    }

    public static Money inr(int amountUnit) {
        return new Money(amountUnit, "INR");
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add Money with different currencies");
        }
        return new Money(this.amountUnit + other.amountUnit, this.currency);
    }

    public Money subtract(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot subtract Money with different currencies");
        }
        return new Money(this.amountUnit - other.amountUnit, this.currency);
    }


}
