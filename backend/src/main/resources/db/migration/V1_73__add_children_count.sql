ALTER TABLE process ADD COLUMN children_count BIGINT NOT NULL DEFAULT 0;
ALTER TABLE process_aud ADD COLUMN children_count BIGINT NULL;
