package ch.hslu.wipro.politassistant.adapter.in.rest.error;

import java.time.LocalDateTime;

public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}