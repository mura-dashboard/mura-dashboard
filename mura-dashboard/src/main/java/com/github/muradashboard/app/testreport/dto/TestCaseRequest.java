package com.github.muradashboard.app.testreport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "A single test case (method) execution within a test suite")
public record TestCaseRequest(
        @Schema(description = "The test (method) name, e.g. `annotationIsAssignableToMethodAndLocalVariable()`",
                example = "annotationIsAssignableToMethodAndLocalVariable()")
        String name,

        @Schema(description = "The class name, e.g `com.example.testing.annotations.TestingBestPracticeTest`",
                example = "com.example.testing.annotations.TestingBestPracticeTest")
        String classname,

        @Schema(description = "The time it took to execute the test (method) in seconds",
                example = "0.03")
        double time,

        @Schema(description = "If the test failed, this contains the failure details; null if the test passed",
                nullable = true)
        FailureRequest failure
) {
}
