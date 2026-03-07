package com.github.muradashboard.app.testreport;

import com.github.muradashboard.app.testreport.dto.TestReportRequest;
import com.github.muradashboard.app.testreport.dto.TestReportResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test-reports")
public class TestReportController {

    private final TestReportService testReportService;

    public TestReportController(TestReportService testReportService) {
        this.testReportService = testReportService;
    }

    @PostMapping
    public ResponseEntity<TestReportResponse> submitReport(@RequestBody TestReportRequest request) {
        var response = testReportService.submit(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
