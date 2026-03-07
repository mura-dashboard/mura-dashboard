package com.github.muradashboard.app.testreport;

import com.github.muradashboard.app.testreport.dto.*;
import com.github.muradashboard.app.testreport.dto.TestReportResponse.FlakyTest;
import com.github.muradashboard.app.testreport.entity.TestCaseEntity;
import com.github.muradashboard.app.testreport.entity.TestReportEntity;
import com.github.muradashboard.app.testreport.entity.TestSuiteEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TestReportService {

    private final TestReportRepository repository;

    public TestReportService(TestReportRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public TestReportResponse submit(TestReportRequest request) {
        Set<String> flakyTestIds = detectFlakyTestIds(request);

        var report = mapToEntity(request, flakyTestIds);
        var saved = repository.save(report);

        List<FlakyTest> flakyTests = buildFlakyTestResponse(request, flakyTestIds);
        return new TestReportResponse(saved.getId(), flakyTests);
    }

    private Set<String> detectFlakyTestIds(TestReportRequest request) {
        Map<String, Integer> failuresCount = new HashMap<>();
        Map<String, Integer> successCount = new HashMap<>();

        request.testsuites().stream()
                .filter(suite -> suite.failures() > 0 || suite.errors() > 0)
                .flatMap(suite -> suite.testcases().stream())
                .forEach(tc -> {
                    String id = tc.classname() + "#" + tc.name();
                    if (tc.failure() != null) {
                        failuresCount.merge(id, 1, Integer::sum);
                    } else {
                        successCount.merge(id, 1, Integer::sum);
                    }
                });

        return failuresCount.keySet().stream()
                .filter(successCount::containsKey)
                .collect(java.util.stream.Collectors.toSet());
    }

    private List<FlakyTest> buildFlakyTestResponse(TestReportRequest request, Set<String> flakyTestIds) {
        Map<String, Integer> retriesCount = new HashMap<>();

        request.testsuites().stream()
                .filter(suite -> suite.failures() > 0 || suite.errors() > 0)
                .flatMap(suite -> suite.testcases().stream())
                .filter(tc -> tc.failure() != null)
                .forEach(tc -> {
                    String id = tc.classname() + "#" + tc.name();
                    if (flakyTestIds.contains(id)) {
                        retriesCount.merge(id, 1, Integer::sum);
                    }
                });

        return retriesCount.entrySet().stream()
                .map(entry -> new FlakyTest(entry.getKey(), entry.getValue()))
                .toList();
    }

    private TestReportEntity mapToEntity(TestReportRequest request, Set<String> flakyTestIds) {
        var report = TestReportEntity.builder()
                .name(request.name())
                .modulePath(request.modulePath())
                .testTaskName(request.testTaskName())
                .build();

        for (TestSuiteRequest suiteReq : request.testsuites()) {
            var suite = TestSuiteEntity.builder()
                    .testReport(report)
                    .name(suiteReq.name())
                    .tests(suiteReq.tests())
                    .skipped(suiteReq.skipped())
                    .failures(suiteReq.failures())
                    .errors(suiteReq.errors())
                    .timestamp(suiteReq.timestamp())
                    .time(suiteReq.time())
                    .build();

            for (TestCaseRequest caseReq : suiteReq.testcases()) {
                String testId = caseReq.classname() + "#" + caseReq.name();

                var testCase = TestCaseEntity.builder()
                        .testSuite(suite)
                        .name(caseReq.name())
                        .classname(caseReq.classname())
                        .time(caseReq.time())
                        .flaky(flakyTestIds.contains(testId))
                        .build();

                if (caseReq.failure() instanceof FailureRequest(String message, String type, String details)) {
                    testCase.setFailureMessage(message);
                    testCase.setFailureType(type);
                    testCase.setFailureDetails(details);
                }

                suite.getTestCases().add(testCase);
            }

            report.getTestSuites().add(suite);
        }

        return report;
    }
}
