package com.github.muradashboard.app.testreport.entity;

import jakarta.persistence.*;
import lombok.*;

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

    private String name;

    private String classname;

    private double time;

    @Builder.Default
    private boolean flaky = false;

    private String failureMessage;

    private String failureType;

    private String failureDetails;
}
