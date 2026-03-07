package com.github.muradashboard.app.testreport.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * A single test case (method) execution within a {@link TestSuiteEntity}.
 */
@Entity
@Table(name = "test_case")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_suite_id", nullable = false)
    private TestSuiteEntity testSuite;

    /**
     * The test (method) name, e.g. {@code annotationIsAssignableToMethodAndLocalVariable()}.
     */
    private String name;

    /**
     * The class name, e.g. {@code com.example.testing.annotations.TestingBestPracticeTest}.
     */
    private String classname;

    /**
     * The time it took to execute the test (method) in seconds.
     */
    private double time;

    /**
     * Whether this test was detected as flaky. A test is flaky if it both
     * failed and succeeded across retries within the same report.
     */
    @Builder.Default
    private boolean flaky = false;

    /**
     * The failure message, e.g. {@code org.opentest4j.AssertionFailedError: expected: 2 but was: 0}.
     * {@code null} if the test passed.
     */
    private String failureMessage;

    /**
     * The type of the failure, e.g. {@code org.opentest4j.AssertionFailedError}.
     * {@code null} if the test passed.
     */
    private String failureType;

    /**
     * The details of the failure, e.g. the assertion error stack trace.
     * {@code null} if the test passed.
     */
    private String failureDetails;
}
