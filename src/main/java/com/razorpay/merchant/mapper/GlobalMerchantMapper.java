package com.razorpay.merchant.mapper;

import com.razorpay.merchant.dto.response.ApiKeyResponse;
import com.razorpay.merchant.dto.response.CreateApiKeyResponse;
import com.razorpay.merchant.dto.response.MerchantResponse;
import com.razorpay.merchant.entity.ApiKey;
import com.razorpay.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GlobalMerchantMapper {

    @Mapping(target = "id", source = "merchant.id")
    CreateApiKeyResponse toCreateApiKeyResponse(ApiKey apiKey);

    List<ApiKeyResponse> toApiKeyResponseList(List<ApiKey> apiKeys);

    MerchantResponse toMerchantResponse(Merchant merchant);

}
