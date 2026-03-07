package com.github.muradashboard.app.testreport.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A test suite representing a single test class execution within a {@link TestReportEntity}.
 *
 * <p>Each suite corresponds to a test class and contains the individual test case (method) results.</p>
 */
@Entity
@Table(name = "test_suite")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestSuiteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_report_id", nullable = false)
    private TestReportEntity testReport;

    /**
     * The test class name, e.g. {@code com.example.testing.annotations.TestingBestPracticeTest}.
     */
    private String name;

    /**
     * The number of tests (methods) in the suite.
     */
    private int tests;

    /**
     * The number of skipped tests (methods) in the suite.
     */
    private int skipped;

    /**
     * The number of failed tests (methods) in the suite.
     */
    private int failures;

    /**
     * The number of errors in the suite.
     */
    private int errors;

    /**
     * The timestamp when the suite was executed.
     */
    @Column(name = "\"timestamp\"")
    private Instant timestamp;

    /**
     * The time it took to execute the suite in seconds.
     */
    private double time;

    /**
     * The list of test cases (tests/methods) within this suite/class.
     */
    @OneToMany(mappedBy = "testSuite", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TestCaseEntity> testCases = new ArrayList<>();
}
