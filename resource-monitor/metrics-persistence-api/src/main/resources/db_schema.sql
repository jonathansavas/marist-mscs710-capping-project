--
-- File generated with SQLiteStudio v3.2.1 on Tue Feb 25 19:39:19 2020
--
-- Text encoding used: System
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: CPU
CREATE TABLE CPU (
    Datetime    BIGINT  NOT NULL
                        PRIMARY KEY,
    Temperature INTEGER NOT NULL,
    Utilization REAL    NOT NULL
);


-- Table: CPU_Core
CREATE TABLE CPU_Core (
    Datetime         BIGINT  PRIMARY KEY
                             NOT NULL,
    Core_Id          INTEGER NOT NULL,
    Core_Utilization REAL    NOT NULL,
    Core_Temp        INTEGER NOT NULL
);


-- Table: GPU
CREATE TABLE GPU (
    Datetime        BIGINT NOT NULL
                           PRIMARY KEY,
    Utilization     REAL   NOT NULL,
    Mem_Util        REAL   NOT NULL,
    Shared_Mem_Util REAL   NOT NULL,
    Temperature     REAL   NOT NULL
);


-- Table: Memory
CREATE TABLE Memory (
    Datetime    BIGINT  PRIMARY KEY,
    Memory_util REAL    NOT NULL,
    Page_Faults INTEGER NOT NULL
);


-- Table: Network
CREATE TABLE Network (
    Datetime   BIGINT  NOT NULL
                       PRIMARY KEY,
    Throughput INTEGER NOT NULL,
    Send       REAL    NOT NULL,
    Receive    REAL    NOT NULL
);


-- Table: Processess
CREATE TABLE Processess (
    Rec_Id       INTEGER PRIMARY KEY AUTOINCREMENT,
    PID          INTEGER NOT NULL,
    Process_Name TEXT    NOT NULL,
    Start_Time   BIGINT  NOT NULL,
    End_Time     BIGINT,
    metrics      BLOB    NOT NULL
);


-- Table: System_Constants
CREATE TABLE System_Constants (
    Total_Mem      INTEGER NOT NULL,
    Total_Cpu      INTEGER NOT NULL,
    GPU_MEM        INTEGER NOT NULL,
    GPU_ShareD_Mem INTEGER NOT NULL,
    Core_Speed     REAL    NOT NULL
);


-- Table: System_Metrics
CREATE TABLE System_Metrics (
    Datetime  BIGINT PRIMARY KEY
                     NOT NULL,
    Uptime    BIGINT NOT NULL,
    Idle_Time BIGINT NOT NULL
);


COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
