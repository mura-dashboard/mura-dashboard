package com.github.muradashboard.app.testreport.dto;

import java.util.List;

public record TestReportRequest(
        String name,
        String modulePath,
        String testTaskName,
        List<TestSuiteRequest> testsuites
) {
}
