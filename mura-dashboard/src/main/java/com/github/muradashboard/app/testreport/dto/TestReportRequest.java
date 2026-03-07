package com.github.muradashboard.app.testreport.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "A test execution report")
public record TestReportRequest(
        @Schema(description = "A unique logical name, e.g the git repository, project name or application name",
                example = "myproject")
        String name,

        @Schema(description = "The gradle project module path, e.g `:libs:common` or `:app`",
                example = ":modules:search")
        String modulePath,

        @Schema(description = "The gradle test task name, e.g `test`, `integrationTest` or `functionalTest`",
                example = "test")
        String testTaskName,

        @Schema(description = "The list of test suites within this report")
        List<TestSuiteRequest> testsuites
) {
}
