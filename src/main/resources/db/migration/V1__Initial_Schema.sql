-- ================================================================
-- Flyway Migration: V1__Initial_Schema.sql
-- Digital Printing Queue Management System
-- Course: 23IT723 - DevOps Laboratory
-- Applied: Phase 1 & 2 Foundation
-- ================================================================

-- ----------------------------------------------------------------
-- TABLE: users
-- Stores all user accounts (USER and ADMIN roles)
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100)    NOT NULL,
    email       VARCHAR(150)    NOT NULL,
    mobile      VARCHAR(10)     NULL,
    password    VARCHAR(255)    NOT NULL COMMENT 'BCrypt hashed — never plain text',
    role        ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
    status      ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at  DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='User accounts — both USER and ADMIN roles';

CREATE INDEX idx_users_email  ON users(email);
CREATE INDEX idx_users_role   ON users(role);
CREATE INDEX idx_users_status ON users(status);

-- ----------------------------------------------------------------
-- TABLE: printers
-- Virtual printers A, B, C — seeded in V2
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS printers (
    id                    BIGINT          NOT NULL AUTO_INCREMENT,
    name                  VARCHAR(100)    NOT NULL,
    model                 VARCHAR(100)    NOT NULL,
    location              VARCHAR(100)    NOT NULL DEFAULT 'Print Room',
    status                ENUM('IDLE','BUSY','OFFLINE') NOT NULL DEFAULT 'IDLE',
    total_jobs_processed  INT             NOT NULL DEFAULT 0,
    last_active_at        DATETIME(6)     NULL,
    created_at            DATETIME(6)     NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_printers PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Virtual printers simulating physical print devices';

CREATE INDEX idx_printers_status ON printers(status);

-- ----------------------------------------------------------------
-- TABLE: print_jobs
-- All submitted print jobs — central table
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS print_jobs (
    id                BIGINT          NOT NULL AUTO_INCREMENT,
    job_number        VARCHAR(20)     NOT NULL COMMENT 'Format: PJ-YYYYMMDD-NNNN',
    user_id           BIGINT          NOT NULL,
    printer_id        BIGINT          NULL,
    document_name     VARCHAR(255)    NOT NULL,
    original_filename VARCHAR(255)    NULL,
    file_path         VARCHAR(500)    NULL,
    file_size         BIGINT          NULL COMMENT 'File size in bytes',
    file_type         VARCHAR(100)    NULL COMMENT 'MIME type',
    page_count        INT             NOT NULL,
    copies            INT             NOT NULL DEFAULT 1,
    color_mode        ENUM('COLOR','BLACK_WHITE') NOT NULL DEFAULT 'BLACK_WHITE',
    paper_size        ENUM('A4','A3','LETTER')    NOT NULL DEFAULT 'A4',
    status            ENUM('QUEUED','PROCESSING','COMPLETED','FAILED','CANCELLED')
                                     NOT NULL DEFAULT 'QUEUED',
    estimated_cost    DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    queue_position    INT             NULL COMMENT 'Position in queue; NULL when not queued',
    notes             TEXT            NULL,
    submitted_at      DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    started_at        DATETIME(6)    NULL COMMENT 'When printer started processing',
    completed_at      DATETIME(6)    NULL COMMENT 'When job finished (completed/failed/cancelled)',

    CONSTRAINT pk_print_jobs   PRIMARY KEY (id),
    CONSTRAINT uk_job_number   UNIQUE (job_number),
    CONSTRAINT fk_jobs_user    FOREIGN KEY (user_id)   REFERENCES users(id)    ON DELETE RESTRICT,
    CONSTRAINT fk_jobs_printer FOREIGN KEY (printer_id) REFERENCES printers(id) ON DELETE SET NULL,

    CONSTRAINT chk_page_count  CHECK (page_count >= 1 AND page_count <= 500),
    CONSTRAINT chk_copies      CHECK (copies >= 1 AND copies <= 50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Print job queue — core business table';

CREATE INDEX idx_jobs_user_id    ON print_jobs(user_id);
CREATE INDEX idx_jobs_status     ON print_jobs(status);
CREATE INDEX idx_jobs_submitted  ON print_jobs(submitted_at);
CREATE INDEX idx_jobs_printer_id ON print_jobs(printer_id);
CREATE INDEX idx_jobs_number     ON print_jobs(job_number);

-- ----------------------------------------------------------------
-- TABLE: audit_logs
-- Immutable log of all state-changing actions
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS audit_logs (
    id           BIGINT          NOT NULL AUTO_INCREMENT,
    user_id      BIGINT          NULL COMMENT 'NULL for system actions',
    action       VARCHAR(100)    NOT NULL COMMENT 'e.g. USER_REGISTERED, JOB_SUBMITTED',
    entity_type  VARCHAR(50)     NOT NULL COMMENT 'e.g. USER, PRINT_JOB, PRINTER',
    entity_id    BIGINT          NULL,
    old_value    TEXT            NULL COMMENT 'JSON of previous state',
    new_value    TEXT            NULL COMMENT 'JSON of new state',
    ip_address   VARCHAR(45)     NULL,
    created_at   DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_audit_logs   PRIMARY KEY (id),
    CONSTRAINT fk_audit_user   FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Immutable audit trail — records cannot be deleted';

CREATE INDEX idx_audit_user_id    ON audit_logs(user_id);
CREATE INDEX idx_audit_entity     ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_created_at ON audit_logs(created_at);

-- ----------------------------------------------------------------
-- TABLE: app_settings
-- Key-value configuration store (cost prices, queue state, etc.)
-- ----------------------------------------------------------------
CREATE TABLE IF NOT EXISTS app_settings (
    id            BIGINT          NOT NULL AUTO_INCREMENT,
    setting_key   VARCHAR(100)    NOT NULL,
    setting_value VARCHAR(500)    NOT NULL,
    description   VARCHAR(255)    NULL,
    updated_at    DATETIME(6)    NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

    CONSTRAINT pk_app_settings PRIMARY KEY (id),
    CONSTRAINT uk_setting_key  UNIQUE (setting_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Application configuration settings';
