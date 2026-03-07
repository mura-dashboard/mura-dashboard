package com.github.muradashboard.app.testreport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Details of a test case failure")
public record FailureRequest(
        @Schema(description = "The failure message",
                example = "org.opentest4j.AssertionFailedError: expected: 2 but was: 0")
        String message,

        @Schema(description = "The type of the failure",
                example = "org.opentest4j.AssertionFailedError")
        String type,

        @Schema(description = "The details of the failure",
                example = "the assertion error stack trace")
        String details
) {
}
