package com.github.muradashboard.app.testreport;

import com.github.muradashboard.app.presentation.rest.testreport.dto.FlakyTestPageResponse;
import com.github.muradashboard.app.presentation.rest.testreport.dto.FlakyTestSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlakyTestService {

    private final FlakyTestQueryRepository flakyTestQueryRepository;

    @Transactional(readOnly = true)
    public FlakyTestPageResponse getFlakyTests(Instant from, Instant to,
                                                int page, int size,
                                                String sortBy, String order,
                                                List<String> statuses) {
        int offset = page * size;

        List<Object[]> rows = flakyTestQueryRepository.findTests(from, to, statuses, sortBy, order, offset, size);
        long total = flakyTestQueryRepository.countTests(from, to, statuses);

        var content = rows.stream()
                .map(row -> {
                    long flakyCount = ((Number) row[6]).longValue();
                    long failedCount = ((Number) row[9]).longValue();
                    String testStatus = computeTestStatus(flakyCount, failedCount);
                    return new FlakyTestSummary(
                            (String) row[0],  // reportName
                            (String) row[1],  // modulePath
                            (String) row[2],  // testTaskName
                            (String) row[3],  // classname
                            (String) row[4],  // name
                            ((Number) row[5]).longValue(),  // totalRuns
                            flakyCount,
                            failedCount,  // errorCount
                            ((Number) row[7]).doubleValue(),  // flakinessRate
                            toInstant(row[8]),  // lastSeen
                            testStatus
                    );
                })
                .toList();

        int totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 0;

        return new FlakyTestPageResponse(content, page, size, total, totalPages);
    }

    private String computeTestStatus(long flakyCount, long failedCount) {
        if (flakyCount > 0) {
            return "FLAKY";
        } else if (failedCount > 0) {
            return "FAILED";
        } else {
            return "SUCCESSFUL";
        }
    }

    private Instant toInstant(Object value) {
        if (value instanceof Instant instant) {
            return instant;
        }
        if (value instanceof Timestamp ts) {
            return ts.toInstant();
        }
        if (value instanceof java.time.OffsetDateTime odt) {
            return odt.toInstant();
        }
        return Instant.parse(value.toString());
    }
}
