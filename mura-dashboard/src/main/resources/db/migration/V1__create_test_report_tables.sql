CREATE TABLE test_report (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name           VARCHAR(255)             NOT NULL,
    module_path    VARCHAR(255)             NOT NULL,
    test_task_name VARCHAR(255)             NOT NULL,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

CREATE TABLE test_suite (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    test_report_id  BIGINT                   NOT NULL REFERENCES test_report(id),
    name            VARCHAR(500)             NOT NULL,
    tests           INT                      NOT NULL,
    skipped         INT                      NOT NULL,
    failures        INT                      NOT NULL,
    errors          INT                      NOT NULL,
    "timestamp"     TIMESTAMP WITH TIME ZONE NOT NULL,
    time            DOUBLE PRECISION         NOT NULL
);

CREATE TABLE test_case (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    test_suite_id   BIGINT                   NOT NULL REFERENCES test_suite(id),
    name            VARCHAR(1000)            NOT NULL,
    classname       VARCHAR(500)             NOT NULL,
    time            DOUBLE PRECISION         NOT NULL,
    is_flaky        BOOLEAN                  NOT NULL DEFAULT FALSE,
    failure_message TEXT,
    failure_type    VARCHAR(500),
    failure_details TEXT
);
