-- ================================================================
-- Flyway Migration: V4__Phase5_VirtualPrinter.sql
-- Digital Printing Queue Management System
-- Phase 5: Virtual Printer Engine
-- ================================================================

-- Update printer status ENUM
ALTER TABLE printers 
    MODIFY COLUMN status ENUM('IDLE','PRINTING','PAUSED','ERROR','OFFLINE') NOT NULL DEFAULT 'IDLE';

-- Remove the old seed printers and ensure PRINTER-01 exists
DELETE FROM printers;
INSERT INTO printers (name, model, location, status, total_jobs_processed)
VALUES ('PRINTER-01', 'Virtual Print Engine', 'Server Room', 'IDLE', 0);
