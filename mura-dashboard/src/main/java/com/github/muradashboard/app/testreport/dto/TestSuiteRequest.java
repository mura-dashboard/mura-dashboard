package com.github.muradashboard.app.testreport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(description = "A test suite representing a single test class execution")
public record TestSuiteRequest(
        @Schema(description = "The test class name, e.g `com.example.testing.annotations.TestingBestPracticeTest`",
                example = "com.example.testing.annotations.TestingBestPracticeTest")
        String name,

        @Schema(description = "The number of tests (methods) in the suite",
                example = "5")
        int tests,

        @Schema(description = "The number of skipped tests (methods) in the suite",
                example = "0")
        int skipped,

        @Schema(description = "The number of failed tests (methods) in the suite",
                example = "1")
        int failures,

        @Schema(description = "The number of errors in the suite",
                example = "0")
        int errors,

        @Schema(description = "The timestamp when the suite was executed",
                example = "2026-03-06T07:39:12.954Z")
        Instant timestamp,

        @Schema(description = "The time it took to execute the suite in seconds",
                example = "0.123")
        double time,

        @Schema(description = "The list of testcase elements (tests/methods) within the suite/class")
        List<TestCaseRequest> testcases
) {
}
