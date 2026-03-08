CREATE TABLE test_report (
    id             BIGINT GENERATED ALWAYS AS IDENTITY,
    name           VARCHAR(4000)             NOT NULL,
    module_path    VARCHAR(4000)             NOT NULL,
    test_task_name VARCHAR(4000)             NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    CONSTRAINT pk_test_report PRIMARY KEY (id)
);

CREATE TABLE test_suite (
    id              BIGINT GENERATED ALWAYS AS IDENTITY,
    test_report_id  BIGINT                   NOT NULL,
    name            VARCHAR(4000)            NOT NULL,
    tests           INT                      NOT NULL,
    skipped         INT                      NOT NULL,
    failures        INT                      NOT NULL,
    errors          INT                      NOT NULL,
    "timestamp"     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    time            DOUBLE PRECISION         NOT NULL,
    CONSTRAINT pk_test_suite PRIMARY KEY (id),
    CONSTRAINT fk_test_suite_test_report FOREIGN KEY (test_report_id) REFERENCES test_report(id)
);

CREATE TABLE test_case (
    id              BIGINT GENERATED ALWAYS AS IDENTITY,
    test_suite_id   BIGINT                   NOT NULL,
    name            VARCHAR(4000)            NOT NULL,
    classname       VARCHAR(4000)            NOT NULL,
    time            DOUBLE PRECISION         NOT NULL,
    is_flaky        BOOLEAN                  NOT NULL DEFAULT FALSE,
    failure_message TEXT,
    failure_type    TEXT,
    failure_details TEXT,
    CONSTRAINT pk_test_case PRIMARY KEY (id),
    CONSTRAINT fk_test_case_test_suite FOREIGN KEY (test_suite_id) REFERENCES test_suite(id)
);

-- Foreign key indices (joins)
CREATE INDEX idx_test_suite_test_report_id ON test_suite (test_report_id);
CREATE INDEX idx_test_case_test_suite_id ON test_case (test_suite_id);

-- Date range filter on test_report
CREATE INDEX idx_test_report_created_at ON test_report (created_at);

-- GROUP BY classname + name, with is_flaky for the HAVING filter
CREATE INDEX idx_test_case_classname_name ON test_case (classname, name);
CREATE INDEX idx_test_case_is_flaky ON test_case (is_flaky);

CREATE INDEX idx_test_report_grouping ON test_report (name, module_path, test_task_name);