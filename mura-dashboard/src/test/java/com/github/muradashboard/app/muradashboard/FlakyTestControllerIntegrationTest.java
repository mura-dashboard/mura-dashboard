package com.github.muradashboard.app.muradashboard;

import com.github.muradashboard.app.testreport.entity.TestCaseEntity;
import com.github.muradashboard.app.testreport.entity.TestReportEntity;
import com.github.muradashboard.app.testreport.entity.TestSuiteEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(properties = {
        "spring.security.user.name=admin",
        "spring.security.user.password=admin"
})
@AutoConfigureMockMvc
class FlakyTestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        transactionTemplate.executeWithoutResult(status -> {
            entityManager.createNativeQuery("DELETE FROM test_case").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM test_suite").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM test_report").executeUpdate();
        });
    }

    @Test
    void shouldReturnFlakyTestsSortedByFlakyCountDescByDefault() throws Exception {
        Instant now = Instant.now();
        insertFlakyTestData("com.example.AlphaTest", "testA", 5, 3, now.minus(1, ChronoUnit.DAYS));
        insertFlakyTestData("com.example.BetaTest", "testB", 10, 7, now.minus(2, ChronoUnit.DAYS));
        insertFlakyTestData("com.example.GammaTest", "testC", 4, 1, now.minus(3, ChronoUnit.DAYS));

        mockMvc.perform(get("/rapi/flaky-tests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalElements").value(3))
                .andExpect(jsonPath("$.totalPages").value(1))
                // Default sort is flakyCount desc: BetaTest(7) > AlphaTest(3) > GammaTest(1)
                .andExpect(jsonPath("$.content[0].classname").value("com.example.BetaTest"))
                .andExpect(jsonPath("$.content[0].flakyCount").value(7))
                .andExpect(jsonPath("$.content[1].classname").value("com.example.AlphaTest"))
                .andExpect(jsonPath("$.content[1].flakyCount").value(3))
                .andExpect(jsonPath("$.content[2].classname").value("com.example.GammaTest"))
                .andExpect(jsonPath("$.content[2].flakyCount").value(1));
    }

    @Test
    void shouldSortByFlakinessRateAscending() throws Exception {
        Instant now = Instant.now();
        // AlphaTest: 3/5 = 0.60, BetaTest: 7/10 = 0.70, GammaTest: 1/4 = 0.25
        insertFlakyTestData("com.example.AlphaTest", "testA", 5, 3, now.minus(1, ChronoUnit.DAYS));
        insertFlakyTestData("com.example.BetaTest", "testB", 10, 7, now.minus(2, ChronoUnit.DAYS));
        insertFlakyTestData("com.example.GammaTest", "testC", 4, 1, now.minus(3, ChronoUnit.DAYS));

        mockMvc.perform(get("/rapi/flaky-tests")
                        .param("sort", "flakinessRate")
                        .param("order", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                // asc: GammaTest(0.25) < AlphaTest(0.60) < BetaTest(0.70)
                .andExpect(jsonPath("$.content[0].classname").value("com.example.GammaTest"))
                .andExpect(jsonPath("$.content[1].classname").value("com.example.AlphaTest"))
                .andExpect(jsonPath("$.content[2].classname").value("com.example.BetaTest"));
    }

    @Test
    void shouldRespectPagination() throws Exception {
        Instant now = Instant.now();
        for (int i = 1; i <= 5; i++) {
            insertFlakyTestData("com.example.Test" + i, "test" + i, 10, i, now.minus(1, ChronoUnit.DAYS));
        }

        // Page 0, size 2, sort by flakyCount desc: Test5(5), Test4(4)
        mockMvc.perform(get("/rapi/flaky-tests")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "flakyCount")
                        .param("order", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.content[0].flakyCount").value(5))
                .andExpect(jsonPath("$.content[1].flakyCount").value(4));

        // Page 2 (last page): Test1(1)
        mockMvc.perform(get("/rapi/flaky-tests")
                        .param("page", "2")
                        .param("size", "2")
                        .param("sort", "flakyCount")
                        .param("order", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].flakyCount").value(1));
    }

    @Test
    void shouldFilterByDateRange() throws Exception {
        Instant now = Instant.now();
        // One report from 2 days ago (within default 7-day range)
        insertFlakyTestData("com.example.RecentTest", "testRecent", 5, 3, now.minus(2, ChronoUnit.DAYS));
        // One report from 20 days ago (outside default 7-day range)
        insertFlakyTestData("com.example.OldTest", "testOld", 8, 6, now.minus(20, ChronoUnit.DAYS));

        // Default: last 7 days — should only return RecentTest
        mockMvc.perform(get("/rapi/flaky-tests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].classname").value("com.example.RecentTest"));

        // Explicit wide range — should return both
        String from = now.minus(30, ChronoUnit.DAYS).toString();
        String to = now.plus(1, ChronoUnit.DAYS).toString();
        mockMvc.perform(get("/rapi/flaky-tests")
                        .param("from", from)
                        .param("to", to))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    void shouldReturnEmptyPageWhenNoFlakyTests() throws Exception {
        Instant now = Instant.now();
        // Insert non-flaky test data (0 flaky out of 5 runs)
        insertFlakyTestData("com.example.StableTest", "testStable", 5, 0, now.minus(1, ChronoUnit.DAYS));

        mockMvc.perform(get("/rapi/flaky-tests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void shouldAggregateAcrossMultipleReports() throws Exception {
        Instant now = Instant.now();
        // Same test class+name across multiple reports should be aggregated
        insertFlakyTestData("com.example.FlakyTest", "testFlaky", 3, 2, now.minus(1, ChronoUnit.DAYS));
        insertFlakyTestData("com.example.FlakyTest", "testFlaky", 4, 1, now.minus(2, ChronoUnit.DAYS));

        mockMvc.perform(get("/rapi/flaky-tests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].classname").value("com.example.FlakyTest"))
                .andExpect(jsonPath("$.content[0].totalRuns").value(7))       // 3 + 4
                .andExpect(jsonPath("$.content[0].flakyCount").value(3));     // 2 + 1
    }

    @Test
    void shouldFallBackToDefaultSortForInvalidColumn() throws Exception {
        Instant now = Instant.now();
        insertFlakyTestData("com.example.AlphaTest", "testA", 5, 3, now.minus(1, ChronoUnit.DAYS));
        insertFlakyTestData("com.example.BetaTest", "testB", 10, 7, now.minus(2, ChronoUnit.DAYS));

        // Invalid sort column silently falls back to flakyCount
        mockMvc.perform(get("/rapi/flaky-tests")
                        .param("sort", "DROP_TABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                // Falls back to flakycount desc: BetaTest(7) > AlphaTest(3)
                .andExpect(jsonPath("$.content[0].classname").value("com.example.BetaTest"))
                .andExpect(jsonPath("$.content[1].classname").value("com.example.AlphaTest"));
    }

    /**
     * Inserts test data: a report containing a single suite with {@code totalRuns}
     * test cases for the given class/name. {@code flakyCount} of those will be marked
     * as flaky (is_flaky = true), the rest will not.
     */
    private void insertFlakyTestData(String classname, String name, int totalRuns, int flakyCount, Instant createdAt) {
        transactionTemplate.executeWithoutResult(status -> {
            TestReportEntity report = new TestReportEntity();
            report.setName("report-" + classname);
            report.setModulePath(":");
            report.setTestTaskName("test");
            report.setCreatedAt(createdAt);

            TestSuiteEntity suite = new TestSuiteEntity();
            suite.setName("suite-" + classname);
            suite.setTests(totalRuns);
            suite.setSkipped(0);
            suite.setFailures(0);
            suite.setErrors(0);
            suite.setTimestamp(createdAt);
            suite.setTime(1.0);
            suite.setTestReport(report);

            for (int i = 0; i < totalRuns; i++) {
                TestCaseEntity testCase = new TestCaseEntity();
                testCase.setClassname(classname);
                testCase.setName(name);
                testCase.setTime(0.1);
                testCase.setFlaky(i < flakyCount);
                testCase.setTestSuite(suite);
                suite.getTestCases().add(testCase);
            }

            report.getTestSuites().add(suite);
            entityManager.persist(report);
        });
    }
}
