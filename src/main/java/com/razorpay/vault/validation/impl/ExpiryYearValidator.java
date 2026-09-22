package com.razorpay.vault.validation.impl;

import com.razorpay.vault.validation.ExpiryYear;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExpiryYearValidator implements ConstraintValidator<ExpiryYear, Integer> {

    @Override
    public boolean isValid(Integer expiryYear, ConstraintValidatorContext context) {
        if (expiryYear == null) return true;

        int currentYear = java.time.Year.now().getValue(); // 2026
        int fullYear = expiryYear < 100 ? 2000 + expiryYear : expiryYear;

        return fullYear >= currentYear;
    }
}