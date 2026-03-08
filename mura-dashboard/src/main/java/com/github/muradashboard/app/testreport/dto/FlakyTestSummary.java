package com.github.muradashboard.app.testreport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Aggregated flaky test summary")
public record FlakyTestSummary(
        @Schema(description = "Fully qualified class name") String classname,
        @Schema(description = "Test method name") String name,
        @Schema(description = "Total number of runs in the date range") long totalRuns,
        @Schema(description = "Number of times the test was marked flaky") long flakyCount,
        @Schema(description = "Flakiness rate (flakyCount / totalRuns)", example = "0.25") double flakinessRate,
        @Schema(description = "Last time this test was seen") Instant lastSeen
) {
}
