package com.razorpay.common.exception;

import com.razorpay.common.exception.enums.ErrorCode;
import lombok.Getter;

@Getter
public class ResourceNotFoundException extends BaseBusinessException {

    private final String resourceName;
    private final Object identifier;

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(ErrorCode.RESOURCE_NOT_FOUND, "%s not found for identifier: [%s]".formatted(resourceName, identifier));
        this.resourceName = resourceName;
        this.identifier = identifier;
    }

}
