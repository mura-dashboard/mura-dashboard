package com.github.muradashboard.app.testreport.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    private String name;

    private int tests;

    private int skipped;

    private int failures;

    private int errors;

    @Column(name = "\"timestamp\"")
    private Instant timestamp;

    private double time;

    @OneToMany(mappedBy = "testSuite", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TestCaseEntity> testCases = new ArrayList<>();
}
