package com.github.muradashboard.app.testreport.dto;

public record TestCaseRequest(
        String name,
        String classname,
        double time,
        FailureRequest failure
) {
}
