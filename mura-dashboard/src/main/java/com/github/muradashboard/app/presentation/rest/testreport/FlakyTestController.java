package com.github.muradashboard.app.presentation.rest.testreport;

import com.github.muradashboard.app.presentation.rest.testreport.dto.FlakyTestPageResponse;
import com.github.muradashboard.app.testreport.FlakyTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/rapi/flaky-tests")
@RequiredArgsConstructor
@Tag(name = "Flaky Tests", description = "Endpoints for querying flaky test data")
public class FlakyTestController {

    private final FlakyTestService flakyTestService;

    @GetMapping
    @Operation(summary = "List flaky tests with pagination, sorting, and date range filter")
    public ResponseEntity<FlakyTestPageResponse> getFlakyTests(
            @Parameter(description = "Start of date range (ISO-8601). Defaults to 7 days ago.")
            @RequestParam(required = false) Instant from,

            @Parameter(description = "End of date range (ISO-8601). Defaults to now.")
            @RequestParam(required = false) Instant to,

            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size,

            @Parameter(description = "Sort field: flakyCount, flakinessRate, totalRuns, lastSeen, classname, name")
            @RequestParam(defaultValue = "flakinessRate") String sort,

            @Parameter(description = "Sort direction: asc or desc")
            @RequestParam(defaultValue = "desc") String order
    ) {
        Instant now = Instant.now();
        if (to == null) {
            to = now;
        }
        if (from == null) {
            from = to.minus(7, ChronoUnit.DAYS);
        }

        FlakyTestPageResponse response = flakyTestService.getFlakyTests(from, to, page, size, sort, order);
        return ResponseEntity.ok(response);
    }
}
