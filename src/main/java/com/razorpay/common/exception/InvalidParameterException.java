package com.razorpay.common.exception;

import lombok.Getter;

@Getter
public class InvalidParameterException extends BaseBusinessException  {

    private final String parameterName;
    private final Object invalidValue;

    public InvalidParameterException(String message, String parameterName, Object invalidValue) {
        super(ErrorCode.BAD_REQUEST_PARAMETER, message);
        this.parameterName = parameterName;
        this.invalidValue = invalidValue;
    }
}
