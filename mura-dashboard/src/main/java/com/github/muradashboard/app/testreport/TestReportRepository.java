package com.github.muradashboard.app.testreport;

import com.github.muradashboard.app.testreport.entity.TestReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestReportRepository extends JpaRepository<TestReportEntity, Long> {
}
