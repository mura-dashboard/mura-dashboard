package com.github.muradashboard.app.testreport.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * A test execution report representing a single test run.
 */
@Entity
@Table(name = "test_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * A unique logical name, e.g. the git repository, project name or application name.
     */
    private String name;

    /**
     * The project module path, e.g. {@code :libs:common} or {@code :app}.
     */
    private String modulePath;

    /**
     * The test task name, e.g. {@code test}, {@code integrationTest} or {@code functionalTest}.
     */
    private String testTaskName;

    @Builder.Default
    private Instant createdAt = Instant.now();

    /**
     * The list of test suites within this report.
     */
    @OneToMany(mappedBy = "testReport", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TestSuiteEntity> testSuites = new ArrayList<>();
}
