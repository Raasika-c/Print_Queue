-- ================================================================
-- Flyway Migration: V2__Seed_Data.sql
-- Seeds initial data: 3 virtual printers + app settings
-- ================================================================

-- ----------------------------------------------------------------
-- Seed: 3 Virtual Printers (A, B, C)
-- ----------------------------------------------------------------
INSERT INTO printers (name, model, location, status, total_jobs_processed)
VALUES
    ('Printer-A', 'HP LaserJet Pro M404dn',  'Print Room - Bay 1', 'IDLE', 0),
    ('Printer-B', 'Canon imageCLASS MF445dw','Print Room - Bay 2', 'IDLE', 0),
    ('Printer-C', 'Epson EcoTank L3250',      'Print Room - Bay 3', 'IDLE', 0);

-- ----------------------------------------------------------------
-- Seed: Application Settings
-- ----------------------------------------------------------------
INSERT INTO app_settings (setting_key, setting_value, description)
VALUES
    ('COLOR_PRICE_PER_PAGE',     '5.00',      'Price per page for COLOR printing (Rs.)'),
    ('BW_PRICE_PER_PAGE',        '1.00',      'Price per page for BLACK_WHITE printing (Rs.)'),
    ('MAX_FILE_SIZE_MB',         '10',        'Maximum allowed file upload size in megabytes'),
    ('PROCESSING_SECONDS_PER_PAGE', '1',      'Simulated processing time per page (seconds)'),
    ('QUEUE_ENABLED',            'true',      'Whether the print queue is active (true/false)'),
    ('DAILY_JOB_COUNTER',        '0',         'Sequential counter for job number generation (resets daily)'),
    ('COUNTER_DATE',             '1970-01-01','Date of last job counter reset (YYYY-MM-DD)');
