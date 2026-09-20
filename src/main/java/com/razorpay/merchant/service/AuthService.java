package com.razorpay.merchant.service;

import com.razorpay.merchant.dto.request.MerchantRequest;
import com.razorpay.merchant.dto.response.MerchantResponse;

public interface AuthService {

    MerchantResponse signUpMerchant(MerchantRequest request);
}
