package com.razorpay.vault.validation;


import com.razorpay.vault.validation.impl.ExpiryYearValidator;
import jakarta.validation.*;

import jakarta.validation.Constraint;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Constraint(
        validatedBy = {ExpiryYearValidator.class}
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExpiryYear {

    String message() default "Expiry Year cannot be in Past";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
