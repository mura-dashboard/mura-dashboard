package com.github.muradashboard.app.testreport;

import java.time.Instant;

/**
 * Spring Data projection interface for the native flaky-test aggregation query.
 */
public interface FlakyTestProjection {
    String getClassname();
    String getName();
    long getTotalRuns();
    long getFlakyCount();
    double getFlakinessRate();
    Instant getLastSeen();
}
