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
                                                String sortBy, String order) {
        int offset = page * size;

        List<Object[]> rows = flakyTestQueryRepository.findFlakyTests(from, to, sortBy, order, offset, size);
        long total = flakyTestQueryRepository.countFlakyTests(from, to);

        var content = rows.stream()
                .map(row -> new FlakyTestSummary(
                        (String) row[0],
                        (String) row[1],
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).longValue(),
                        ((Number) row[4]).doubleValue(),
                        toInstant(row[5])
                ))
                .toList();

        int totalPages = size > 0 ? (int) Math.ceil((double) total / size) : 0;

        return new FlakyTestPageResponse(content, page, size, total, totalPages);
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
