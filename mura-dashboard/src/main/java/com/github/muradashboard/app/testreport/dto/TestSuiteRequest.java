package com.github.muradashboard.app.testreport.dto;

import java.time.Instant;
import java.util.List;

public record TestSuiteRequest(
        String name,
        int tests,
        int skipped,
        int failures,
        int errors,
        Instant timestamp,
        double time,
        List<TestCaseRequest> testcases
) {
}
