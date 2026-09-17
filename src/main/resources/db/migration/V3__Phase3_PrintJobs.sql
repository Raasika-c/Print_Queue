-- ================================================================
-- Flyway Migration: V3__Phase3_PrintJobs.sql
-- Digital Printing Queue Management System
-- Phase 3: Print Job Management Updates
-- ================================================================

ALTER TABLE print_jobs
    RENAME COLUMN page_count TO number_of_pages;

ALTER TABLE print_jobs
    RENAME COLUMN copies TO number_of_copies;

ALTER TABLE print_jobs
    ADD COLUMN total_pages INT NOT NULL DEFAULT 1 AFTER number_of_copies,
    ADD COLUMN orientation ENUM('PORTRAIT','LANDSCAPE') NOT NULL DEFAULT 'PORTRAIT' AFTER color_mode,
    ADD COLUMN duplex BOOLEAN NOT NULL DEFAULT FALSE AFTER orientation,
    ADD COLUMN priority ENUM('LOW','NORMAL','HIGH') NOT NULL DEFAULT 'NORMAL' AFTER duplex,
    ADD COLUMN progress_percentage INT NOT NULL DEFAULT 0 AFTER queue_position,
    ADD COLUMN current_page INT NOT NULL DEFAULT 0 AFTER progress_percentage,
    ADD COLUMN cancelled_at DATETIME(6) NULL AFTER completed_at,
    ADD COLUMN error_message TEXT NULL AFTER cancelled_at;

ALTER TABLE print_jobs 
    MODIFY COLUMN status ENUM('SUBMITTED','QUEUED','PRINTING','PAUSED','COMPLETED','FAILED','CANCELLED') NOT NULL DEFAULT 'SUBMITTED';
