package com.razorpay.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "The requested resource could not be located."),
    BAD_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST, "The provided request parameters are invalid or breached constraints."),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "A resource with the same identifier already exists in the system."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected system error occurred on our end.");

    private final HttpStatus httpStatus;
    private final String defaultDescription;

    ErrorCode(HttpStatus httpStatus, String defaultDescription) {
        this.httpStatus = httpStatus;
        this.defaultDescription = defaultDescription;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getDefaultDescription() {
        return defaultDescription;
    }
}
