package com.github.muradashboard.app.testreport;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FlakyTestQueryRepository {

    private static final Set<String> ALLOWED_SORT_COLUMNS = Set.of(
            "flakycount", "flakinessrate", "totalruns", "lastseen", "classname", "name"
    );

    private static final Set<String> ALLOWED_STATUSES = Set.of("FLAKY", "FAILED", "SUCCESSFUL");

    private final EntityManager em;

    private static final String BASE_SELECT = """
            SELECT tc.classname AS classname,
                   tc.name AS name,
                   COUNT(DISTINCT tr.id) AS totalRuns,
                   COUNT(DISTINCT CASE WHEN tc.is_flaky THEN tr.id END) AS flakyCount,
                   CAST(COUNT(DISTINCT CASE WHEN tc.is_flaky THEN tr.id END) AS double precision) / COUNT(DISTINCT tr.id) AS flakinessRate,
                   MAX(tr.created_at) AS lastSeen,
                   COUNT(DISTINCT CASE WHEN tc.failure_message IS NOT NULL AND NOT tc.is_flaky THEN tr.id END) AS failedCount
            FROM test_case tc
            JOIN test_suite ts ON tc.test_suite_id = ts.id
            JOIN test_report tr ON ts.test_report_id = tr.id
            WHERE tr.created_at >= :from
              AND tr.created_at <= :to
            GROUP BY tc.classname, tc.name
            """;

    private static final String COUNT_SELECT = """
            SELECT COUNT(*) FROM (
                SELECT tc.classname, tc.name
                FROM test_case tc
                JOIN test_suite ts ON tc.test_suite_id = ts.id
                JOIN test_report tr ON ts.test_report_id = tr.id
                WHERE tr.created_at >= :from
                  AND tr.created_at <= :to
                GROUP BY tc.classname, tc.name
            """;

    private static final String COUNT_SUFFIX = ") sub";

    @SuppressWarnings("unchecked")
    public List<Object[]> findTests(Instant from, Instant to,
                                    List<String> statuses,
                                    String sortColumn, String sortDirection,
                                    int offset, int limit) {
        String safeSort = ALLOWED_SORT_COLUMNS.contains(sortColumn.toLowerCase())
                ? sortColumn.toLowerCase()
                : "flakycount";
        String safeDir = "asc".equalsIgnoreCase(sortDirection) ? "ASC" : "DESC";

        String having = buildHavingClause(statuses);
        String sql = BASE_SELECT + having + " ORDER BY " + safeSort + " " + safeDir + " LIMIT :limit OFFSET :offset";

        Query query = em.createNativeQuery(sql);
        query.setParameter("from", from);
        query.setParameter("to", to);
        query.setParameter("limit", limit);
        query.setParameter("offset", offset);

        return query.getResultList();
    }

    public long countTests(Instant from, Instant to, List<String> statuses) {
        String having = buildHavingClause(statuses);
        String sql = COUNT_SELECT + having + COUNT_SUFFIX;

        Query query = em.createNativeQuery(sql);
        query.setParameter("from", from);
        query.setParameter("to", to);

        return ((Number) query.getSingleResult()).longValue();
    }

    private String buildHavingClause(List<String> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return "";
        }

        List<String> validStatuses = statuses.stream()
                .map(String::toUpperCase)
                .filter(ALLOWED_STATUSES::contains)
                .toList();

        if (validStatuses.isEmpty() || validStatuses.size() == ALLOWED_STATUSES.size()) {
            return "";
        }

        List<String> conditions = new ArrayList<>();
        for (String status : validStatuses) {
            switch (status) {
                case "FLAKY" -> conditions.add(
                        "COUNT(DISTINCT CASE WHEN tc.is_flaky THEN tr.id END) > 0");
                case "FAILED" -> conditions.add(
                        "(COUNT(DISTINCT CASE WHEN tc.is_flaky THEN tr.id END) = 0 AND COUNT(DISTINCT CASE WHEN tc.failure_message IS NOT NULL THEN tr.id END) > 0)");
                case "SUCCESSFUL" -> conditions.add(
                        "(COUNT(DISTINCT CASE WHEN tc.is_flaky THEN tr.id END) = 0 AND COUNT(DISTINCT CASE WHEN tc.failure_message IS NOT NULL THEN tr.id END) = 0)");
                default -> throw new UnsupportedOperationException("Unknown status: " + status);
            }
        }

        if (conditions.isEmpty()) {
            return "";
        }

        return " HAVING " + String.join(" OR ", conditions);
    }
}
