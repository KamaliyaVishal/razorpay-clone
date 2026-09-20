package com.razorpay.common.exception;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends BaseBusinessException {

    private final String conflictField;
    private final Object duplicatedValue;

    public DuplicateResourceException(String message, String conflictField, Object duplicatedValue) {
        super(ErrorCode.DUPLICATE_RESOURCE, message);
        this.conflictField = conflictField;
        this.duplicatedValue = duplicatedValue;
    }

}
