ALTER TABLE process MODIFY COLUMN time_spend_per_occurance DECIMAL(8, 2) NOT NULL DEFAULT 0;
ALTER TABLE process_aud MODIFY COLUMN time_spend_per_occurance DECIMAL(8, 2) NULL;
