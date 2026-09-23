package com.razorpay.merchant.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "email is required")
        @Email
        String email,

        @NotBlank(message = "Password is required")
        String password
) {
}
