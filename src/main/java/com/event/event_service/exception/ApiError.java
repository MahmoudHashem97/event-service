package com.event.event_service.exception;

public record ApiError(
        int status,
        String errorCode,
        String message
) {
}
