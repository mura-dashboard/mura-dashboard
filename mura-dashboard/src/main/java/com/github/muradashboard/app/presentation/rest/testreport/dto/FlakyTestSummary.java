package com.github.muradashboard.app.presentation.rest.testreport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Aggregated test summary")
public record FlakyTestSummary(
        @Schema(description = "Test report name (project identifier)") String reportName,
        @Schema(description = "Module path within the project") String modulePath,
        @Schema(description = "Test task name (e.g. test, integrationTest)") String testTaskName,
        @Schema(description = "Fully qualified class name") String classname,
        @Schema(description = "Test method name") String name,
        @Schema(description = "Total number of runs in the date range") long totalRuns,
        @Schema(description = "Number of times the test was marked flaky") long flakyCount,
        @Schema(description = "Flakiness rate (flakyCount / totalRuns)", example = "0.25") double flakinessRate,
        @Schema(description = "Last time this test was seen") Instant lastSeen,
        @Schema(description = "Test status: FLAKY, FAILED, or SUCCESSFUL") String testStatus
) {
}
