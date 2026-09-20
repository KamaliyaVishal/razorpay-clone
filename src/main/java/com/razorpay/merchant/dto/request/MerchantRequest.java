package com.razorpay.merchant.dto.request;


import com.razorpay.common.enums.BusinessType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MerchantRequest(

        @NotNull(message = "Name cannot be null")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name,

        @NotNull(message = "Email cannot be null")
        @Email(message = "Email should be valid")
        String email,

        @NotNull(message = "Password cannot be null")
        @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
        String password,

        String businessName,

        BusinessType businessType
) {
}
