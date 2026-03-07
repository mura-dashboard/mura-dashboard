package com.github.muradashboard.app.testreport.dto;

public record FailureRequest(
        String message,
        String type,
        String details
) {
}
