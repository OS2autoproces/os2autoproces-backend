CREATE TABLE process (
  -- ids
  id                                     BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  local_id                               VARCHAR(64),
  kl_id                                  VARCHAR(64),
  esdh_reference                         VARCHAR(300),

  -- metadata
  phase                                  VARCHAR(64) NOT NULL,
  process_type                           VARCHAR(64) NOT NULL,
  status                                 VARCHAR(64) NOT NULL,
  status_text                            TEXT, -- max(1200)
  created                                TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_changed                           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  decommissioned                         TIMESTAMP NULL,
  visibility                             VARCHAR(64) NOT NULL,
  legal_clause                           VARCHAR(140),
  legal_clause_last_verified             TIMESTAMP NULL,
  kle                                    VARCHAR(8),
  form                                   VARCHAR(11),
  kla                                    VARCHAR(14),
  kla_process                            BOOLEAN NOT NULL,
  freetext                               CHAR(1) NULL, -- not used, needed to trick Spring Data Rest into creating a Predicate

  -- relationships
  reporter                               BIGINT NOT NULL,
  contact                                BIGINT NOT NULL,
  owner                                  BIGINT NOT NULL,
  vendor                                 VARCHAR(255),
  cvr                                    VARCHAR(8) NOT NULL,
  municipality_name                      VARCHAR(64) NOT NULL,

  -- actual process data
  title                                  VARCHAR(50) NOT NULL,
  short_description                      VARCHAR(140) NOT NULL,
  long_description                       VARCHAR(1200),
  process_challenges                     TEXT,
  solution_requests                      TEXT,
  internal_notes                         TEXT,
  time_spend_occurances_per_employee     BIGINT NOT NULL DEFAULT 0,
  time_spend_per_occurance               BIGINT NOT NULL DEFAULT 0,
  time_spend_employees_doing_process     BIGINT NOT NULL DEFAULT 0,
  time_spend_percentage_digital          BIGINT NOT NULL DEFAULT 0,
  time_spend_computed_total              BIGINT NOT NULL DEFAULT 0,
  time_spend_comment                     VARCHAR(300),
  targets_companies                      BOOLEAN NOT NULL,
  targets_citizens                       BOOLEAN NOT NULL,
  level_of_professional_assessment       VARCHAR(64) NOT NULL,
  level_of_change                        VARCHAR(64) NOT NULL,
  level_of_structured_information        VARCHAR(64) NOT NULL,
  level_of_uniformity                    VARCHAR(64) NOT NULL,
  level_of_digital_information           VARCHAR(64) NOT NULL,
  level_of_quality                       VARCHAR(64) NOT NULL,
  level_of_speed                         VARCHAR(64) NOT NULL,
  level_of_routine_work_reduction        VARCHAR(64) NOT NULL,
  evaluated_level_of_roi                 VARCHAR(64) NOT NULL,
  technical_implementation_notes         TEXT, -- max(3000)
  organizational_implementation_notes    TEXT, -- max(3000)
  rating                                 BIGINT NOT NULL,
  rating_comment                         TEXT, -- max(1200)
  search_words                           TEXT, -- max(1000)

  FOREIGN KEY (reporter) REFERENCES users(id),
  FOREIGN KEY (contact) REFERENCES users(id),
  FOREIGN KEY (owner) REFERENCES users(id)
);

CREATE TABLE process_users (
  user_id                                BIGINT NOT NULL,
  process_id                             BIGINT NOT NULL,

  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (process_id) REFERENCES process(id) ON DELETE CASCADE
);

CREATE TABLE process_ous (
  ou_id                                  BIGINT NOT NULL,
  process_id                             BIGINT NOT NULL,

  FOREIGN KEY (ou_id) REFERENCES ous(id) ON DELETE CASCADE,
  FOREIGN KEY (process_id) REFERENCES process(id) ON DELETE CASCADE
);

CREATE TABLE process_technologies (
  technology_id                          BIGINT NOT NULL,
  process_id                             BIGINT NOT NULL,

  FOREIGN KEY (technology_id) REFERENCES technologies(id) ON DELETE CASCADE,
  FOREIGN KEY (process_id) REFERENCES process(id) ON DELETE CASCADE
);

CREATE TABLE process_it_systems (
  it_system_id                           BIGINT NOT NULL,
  process_id                             BIGINT NOT NULL,

  FOREIGN KEY (it_system_id) REFERENCES it_systems(id) ON DELETE CASCADE,
  FOREIGN KEY (process_id) REFERENCES process(id) ON DELETE CASCADE
);

CREATE TABLE process_links (
  link_id                                BIGINT NOT NULL,
  process_id                             BIGINT NOT NULL,

  FOREIGN KEY (link_id) REFERENCES link(id) ON DELETE CASCADE,
  FOREIGN KEY (process_id) REFERENCES process(id) ON DELETE CASCADE
);

CREATE TABLE process_children (
  child_id                               BIGINT NOT NULL,
  process_id                             BIGINT NOT NULL,

  FOREIGN KEY (child_id) REFERENCES process(id) ON DELETE CASCADE,
  FOREIGN KEY (process_id) REFERENCES process(id) ON DELETE CASCADE
);

CREATE TABLE process_domains (
  process_id                             BIGINT NOT NULL,
  domain                                 VARCHAR(255) NOT NULL,

  FOREIGN KEY (process_id) REFERENCES process(id) ON DELETE CASCADE
);
