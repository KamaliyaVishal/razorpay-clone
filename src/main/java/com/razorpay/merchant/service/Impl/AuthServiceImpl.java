package com.razorpay.merchant.service.Impl;

import com.razorpay.common.enums.MerchantStatus;
import com.razorpay.common.enums.UserRole;
import com.razorpay.common.exception.DuplicateResourceException;
import com.razorpay.merchant.dto.request.MerchantRequest;
import com.razorpay.merchant.dto.response.MerchantResponse;
import com.razorpay.merchant.entity.AppUser;
import com.razorpay.merchant.entity.Merchant;
import com.razorpay.merchant.mapper.GlobalMerchantMapper;
import com.razorpay.merchant.repository.AppUserRepository;
import com.razorpay.merchant.repository.MerchantRepository;
import com.razorpay.merchant.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final MerchantRepository merchantRepository;
    private final GlobalMerchantMapper mapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MerchantResponse signUpMerchant(MerchantRequest request) {
        if (appUserRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Merchant with Email already exists", "Email", request.email());
        }

        //Save the merchant and user details to the database and return the response
        Merchant merchant = mapper.fromMerchantRequest(request);
        merchant.setStatus(MerchantStatus.PENDING_KYC);

        merchantRepository.save(merchant);

        //Create user from signup details
        AppUser appUser = AppUser.builder()
                .email(request.email())
                .passwordHash(request.password())
                .role(UserRole.OWNER)
                .merchant(merchant)
                .build();

        appUserRepository.save(appUser);

        return mapper.toMerchantResponse(merchant);

    }
}
