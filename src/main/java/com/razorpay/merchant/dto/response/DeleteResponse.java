package com.razorpay.merchant.dto.response;

import java.time.LocalDateTime;

public record DeleteResponse(
        String status,
        String message,
        LocalDateTime timestamp
) {

    // Static factory method accepting any entity type
    public static <T> DeleteResponse fromEntity(T entity, String identifier) {
        String entityName = entity.getClass().getSimpleName();

        return new DeleteResponse(
                "Success",
                "%s with ID [%s] was successfully deleted.".formatted(entityName, identifier),
                LocalDateTime.now()
        );
    }
}
