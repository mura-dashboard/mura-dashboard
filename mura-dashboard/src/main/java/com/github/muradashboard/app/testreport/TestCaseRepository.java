package com.github.muradashboard.app.testreport;

import com.github.muradashboard.app.testreport.entity.TestCaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestCaseRepository extends JpaRepository<TestCaseEntity, Long> {
}
