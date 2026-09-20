package com.razorpay.merchant.service;

import com.razorpay.merchant.dto.request.MerchantRequest;
import com.razorpay.merchant.dto.response.MerchantResponse;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthService {

    MerchantResponse signUpMerchant(MerchantRequest request);
}
