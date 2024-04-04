ALTER TABLE it_systems DROP COLUMN system_id;
ALTER TABLE it_systems ADD COLUMN kitos_uuid VARCHAR(36) NULL;
