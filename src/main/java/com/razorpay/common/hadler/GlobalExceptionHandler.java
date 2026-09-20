package com.razorpay.common.hadler;

import com.razorpay.common.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // GENERIC BUSINESS HANDLER: Catches ResourceNotFound, InvalidParameter, and DuplicateResource instantly!
    @ExceptionHandler(BaseBusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessExceptions(BaseBusinessException exception) {
        ErrorCode config = exception.getErrorCode();

        List<ErrorResponse.FieldError> fieldErrors = null;

        // Extract specific metadata fields if they exist at runtime
        if (exception instanceof InvalidParameterException invalidParamEx) {
            fieldErrors = List.of(
                    new ErrorResponse.FieldError(invalidParamEx.getParameterName(), "Rejected Value: " + invalidParamEx.getInvalidValue())
            );
        }
        // Automatically intercept and map the duplicate conflict fields
        else if (exception instanceof DuplicateResourceException duplicateEx) {
            fieldErrors = List.of(
                    new ErrorResponse.FieldError(duplicateEx.getConflictField(), "Already exists: " + duplicateEx.getDuplicatedValue())
            );
        }

        ErrorResponse response = ErrorResponse.of(config.name(), exception.getMessage(), fieldErrors);
        return ResponseEntity.status(config.getHttpStatus()).body(response);
    }

    // FRAMEWORK INTERCEPTOR: Catches Jakarta Bean Validations (@NotNull, @Size, @Email, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException exception) {
        List<ErrorResponse.FieldError> fieldErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> new ErrorResponse.FieldError(f.getField(), f.getDefaultMessage()))
                .toList();

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.BAD_REQUEST_PARAMETER.name(),
                "Input parameter validations violated.",
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    // PRODUCTION SAFETY NET: Protects logs and hides low-level internal driver exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedFailures(Exception exception) {
        log.error("System Failure tracked: ", exception);
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INTERNAL_SERVER_ERROR.name(),
                ErrorCode.INTERNAL_SERVER_ERROR.getDefaultDescription()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

}
