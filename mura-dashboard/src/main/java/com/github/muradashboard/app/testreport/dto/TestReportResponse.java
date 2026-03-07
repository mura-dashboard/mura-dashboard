package com.github.muradashboard.app.testreport.dto;

import java.util.List;

public record TestReportResponse(
        Long reportId,
        List<FlakyTest> flakyTests
) {

    public record FlakyTest(
            String testIdentifier,
            int numberOfRetries
    ) {
    }
}
