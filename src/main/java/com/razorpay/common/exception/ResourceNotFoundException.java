package com.razorpay.common.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final Object identifier;

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(resourceName + " not found " + identifier);
        this.resourceName = resourceName;
        this.identifier = identifier;
    }

}
