ALTER TABLE process ADD COLUMN expected_development_time DECIMAL(10,2) NULL;
ALTER TABLE process_aud ADD COLUMN expected_development_time DECIMAL(10,2) NULL DEFAULT NULL;