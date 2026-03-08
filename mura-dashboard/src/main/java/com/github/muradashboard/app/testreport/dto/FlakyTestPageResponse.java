package com.github.muradashboard.app.testreport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Paginated response of flaky tests")
public record FlakyTestPageResponse(
        @Schema(description = "List of flaky test summaries") List<FlakyTestSummary> content,
        @Schema(description = "Current page number (0-based)") int page,
        @Schema(description = "Page size") int size,
        @Schema(description = "Total number of flaky tests") long totalElements,
        @Schema(description = "Total number of pages") int totalPages
) {
}
