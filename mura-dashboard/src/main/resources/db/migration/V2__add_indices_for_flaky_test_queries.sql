-- Foreign key indices (joins)
CREATE INDEX idx_test_suite_test_report_id ON test_suite (test_report_id);
CREATE INDEX idx_test_case_test_suite_id ON test_case (test_suite_id);

-- Date range filter on test_report
CREATE INDEX idx_test_report_created_at ON test_report (created_at);

-- GROUP BY classname + name, with is_flaky for the HAVING filter
CREATE INDEX idx_test_case_classname_name ON test_case (classname, name);
CREATE INDEX idx_test_case_is_flaky ON test_case (is_flaky);
