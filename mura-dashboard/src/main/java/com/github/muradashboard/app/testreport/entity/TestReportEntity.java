package com.github.muradashboard.app.testreport.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    private String name;

    private String modulePath;

    private String testTaskName;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "testReport", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TestSuiteEntity> testSuites = new ArrayList<>();
}
