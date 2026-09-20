package com.razorpay.common.exception;

import lombok.Getter;

@Getter
public abstract class BaseBusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    protected BaseBusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
