package com.github.muradashboard.app.testreport;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FlakyTestQueryRepository {

    private static final Set<String> ALLOWED_SORT_COLUMNS = Set.of(
            "flakycount", "flakinessrate", "totalruns", "lastseen", "classname", "name"
    );

    private final EntityManager em;

    private static final String BASE_QUERY = """
            SELECT tc.classname AS classname,
                   tc.name AS name,
                   COUNT(*) AS totalRuns,
                   SUM(CASE WHEN tc.is_flaky THEN 1 ELSE 0 END) AS flakyCount,
                   CAST(SUM(CASE WHEN tc.is_flaky THEN 1 ELSE 0 END) AS double precision) / COUNT(*) AS flakinessRate,
                   MAX(tr.created_at) AS lastSeen
            FROM test_case tc
            JOIN test_suite ts ON tc.test_suite_id = ts.id
            JOIN test_report tr ON ts.test_report_id = tr.id
            WHERE tr.created_at >= :from
              AND tr.created_at <= :to
            GROUP BY tc.classname, tc.name
            HAVING SUM(CASE WHEN tc.is_flaky THEN 1 ELSE 0 END) > 0
            """;

    private static final String COUNT_QUERY = """
            SELECT COUNT(*) FROM (
                SELECT tc.classname, tc.name
                FROM test_case tc
                JOIN test_suite ts ON tc.test_suite_id = ts.id
                JOIN test_report tr ON ts.test_report_id = tr.id
                WHERE tr.created_at >= :from
                  AND tr.created_at <= :to
                GROUP BY tc.classname, tc.name
                HAVING SUM(CASE WHEN tc.is_flaky THEN 1 ELSE 0 END) > 0
            ) sub
            """;

    @SuppressWarnings("unchecked")
    public List<Object[]> findFlakyTests(Instant from, Instant to,
                                          String sortColumn, String sortDirection,
                                          int offset, int limit) {
        String safeSort = ALLOWED_SORT_COLUMNS.contains(sortColumn.toLowerCase())
                ? sortColumn.toLowerCase()
                : "flakycount";
        String safeDir = "asc".equalsIgnoreCase(sortDirection) ? "ASC" : "DESC";

        String sql = BASE_QUERY + " ORDER BY " + safeSort + " " + safeDir + " LIMIT :limit OFFSET :offset";

        Query query = em.createNativeQuery(sql);
        query.setParameter("from", from);
        query.setParameter("to", to);
        query.setParameter("limit", limit);
        query.setParameter("offset", offset);

        return query.getResultList();
    }

    public long countFlakyTests(Instant from, Instant to) {
        Query query = em.createNativeQuery(COUNT_QUERY);
        query.setParameter("from", from);
        query.setParameter("to", to);

        return ((Number) query.getSingleResult()).longValue();
    }
}
