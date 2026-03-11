-- Add status column to classes
ALTER TABLE classes
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

