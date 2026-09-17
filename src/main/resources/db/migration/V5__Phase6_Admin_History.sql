-- ================================================================
-- Flyway Migration: V5__Phase6_Admin_History.sql
-- Digital Printing Queue Management System
-- Phase 6: Admin Management & Job History
-- ================================================================

CREATE TABLE print_job_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    print_job_id BIGINT NOT NULL,
    old_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    message VARCHAR(255),
    changed_by VARCHAR(100) NOT NULL,
    changed_at DATETIME NOT NULL,
    CONSTRAINT fk_print_job_history_job FOREIGN KEY (print_job_id) REFERENCES print_jobs(id) ON DELETE CASCADE
);
