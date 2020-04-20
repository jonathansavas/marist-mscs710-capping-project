-- This file should not have in-line comments
-- Statements should begin on a new line (i.e. on a line there
-- should be nothing after a semi colon)

PRAGMA foreign_keys = off;

CREATE TABLE IF NOT EXISTS cpu (
    datetime     BIGINT NOT NULL PRIMARY KEY,
    delta_millis BIGINT NOT NULL,
    temperature  REAL,
    utilization  REAL   NOT NULL
);

CREATE TABLE IF NOT EXISTS cpu_core (
    datetime         BIGINT  NOT NULL,
    delta_millis     BIGINT  NOT NULL,
    core_id          INTEGER NOT NULL,
    core_utilization REAL    NOT NULL,
    PRIMARY KEY (datetime, core_id)
);

CREATE TABLE IF NOT EXISTS memory (
    datetime     BIGINT NOT NULL PRIMARY KEY,
    delta_millis BIGINT NOT NULL,
    utilization  REAL   NOT NULL,
    page_faults  REAL   NOT NULL
);

CREATE TABLE IF NOT EXISTS network (
    datetime     BIGINT NOT NULL PRIMARY KEY,
    delta_millis BIGINT NOT NULL,
    throughput   BIGINT NOT NULL,
    send         REAL   NOT NULL,
    receive      REAL   NOT NULL
);

CREATE TABLE IF NOT EXISTS processes (
    rec_id       INTEGER PRIMARY KEY AUTOINCREMENT,
    datetime     BIGINT  NOT NULL,
    delta_millis BIGINT  NOT NULL,
    pid          INTEGER NOT NULL,
    name         TEXT    NOT NULL,
    start_time   BIGINT  NOT NULL,
    uptime       BIGINT  NOT NULL,
    cpu_usage    REAL    NOT NULL,
    memory       BIGINT  NOT NULL,
    kb_read      REAL    NOT NULL,
    kb_written   REAL    NOT NULL,
    state        TEXT    NOT NULL
);

CREATE TABLE IF NOT EXISTS system_constants (
    total_memory     INTEGER NOT NULL,
    physical_cores   INTEGER NOT NULL,
    logical_cores    INTEGER NOT NULL,
    cpu_speed        REAL    NOT NULL,
    gpu_mem          INTEGER,
    gpu_shared_mem   INTEGER
);

CREATE TABLE IF NOT EXISTS system_metrics (
    datetime     BIGINT NOT NULL PRIMARY KEY,
    delta_millis BIGINT NOT NULL,
    uptime       BIGINT NOT NULL
);

PRAGMA foreign_keys = on;
