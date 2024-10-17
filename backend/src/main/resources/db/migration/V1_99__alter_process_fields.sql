SELECT CONSTRAINT_NAME INTO @constraint_name

FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE

WHERE

    REFERENCED_TABLE_SCHEMA = (SELECT DATABASE())

    AND TABLE_NAME = 'process'

    AND REFERENCED_TABLE_NAME = 'users'

    AND COLUMN_NAME = 'owner';



SET @Command = concat('ALTER TABLE process DROP FOREIGN KEY ', @constraint_name);

PREPARE preparedStatement FROM @Command;

EXECUTE preparedStatement;


ALTER TABLE process MODIFY owner bigint NULL;

ALTER TABLE process ADD CONSTRAINT fk_process_owner FOREIGN KEY (owner) REFERENCES users(id);


ALTER TABLE process MODIFY run_period VARCHAR(64) NOT NULL DEFAULT 'NOT_CHOSEN_YET';
ALTER TABLE process MODIFY targets_companies BOOLEAN NULL;
ALTER TABLE process MODIFY targets_citizens BOOLEAN NULL;

ALTER TABLE process MODIFY time_spend_occurances_per_employee BIGINT NULL;
ALTER TABLE process MODIFY time_spend_per_occurance DECIMAL(10,2) NULL;
ALTER TABLE process MODIFY time_spend_percentage_digital BIGINT NULL;
ALTER TABLE process MODIFY time_spend_employees_doing_process BIGINT NULL;
ALTER TABLE process MODIFY expected_development_time DECIMAL(10,2) NULL;
