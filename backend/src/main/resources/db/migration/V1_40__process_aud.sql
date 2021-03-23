CREATE TABLE revisions (
  id                        BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  timestamp                 BIGINT NOT NULL,
  auditor_id                BIGINT,
  auditor_name              VARCHAR(128)
);

CREATE TABLE process_aud (
  id                                     BIGINT NOT NULL,
  rev                                    BIGINT NOT NULL,
  revtype                                TINYINT,
  
  local_id                               VARCHAR(64),
  kl_id                                  VARCHAR(64),
  esdh_reference                         VARCHAR(300),
  phase                                  VARCHAR(64),
  process_type                           VARCHAR(64),
  status                                 VARCHAR(64),
  status_text                            TEXT,
  created                                TIMESTAMP NULL,
  last_changed                           TIMESTAMP NULL,
  decommissioned                         TIMESTAMP NULL,
  visibility                             VARCHAR(64),
  legal_clause                           VARCHAR(140),
  legal_clause_last_verified             TIMESTAMP NULL,
  kle                                    VARCHAR(8),
  form                                   VARCHAR(11),
  kla                                    VARCHAR(14),
  kla_process                            BOOLEAN NULL,
  freetext                               CHAR(1) NULL,
  reporter                               BIGINT,
  contact                                BIGINT,
  owner                                  BIGINT,
  vendor                                 VARCHAR(255),
  cvr                                    VARCHAR(8),
  municipality_name                      VARCHAR(64),
  title                                  VARCHAR(50),
  short_description                      VARCHAR(140),
  long_description                       VARCHAR(1200),
  process_challenges                     TEXT,
  solution_requests                      TEXT,
  internal_notes                         TEXT,
  time_spend_occurances_per_employee     BIGINT,
  time_spend_per_occurance               BIGINT,
  time_spend_employees_doing_process     BIGINT,
  time_spend_percentage_digital          BIGINT,
  time_spend_computed_total              BIGINT,
  time_spend_comment                     VARCHAR(300),
  targets_companies                      BOOLEAN NULL,
  targets_citizens                       BOOLEAN NULL,
  level_of_professional_assessment       VARCHAR(64),
  level_of_change                        VARCHAR(64),
  level_of_structured_information        VARCHAR(64),
  level_of_uniformity                    VARCHAR(64),
  level_of_digital_information           VARCHAR(64),
  level_of_quality                       VARCHAR(64),
  level_of_speed                         VARCHAR(64),
  level_of_routine_work_reduction        VARCHAR(64),
  evaluated_level_of_roi                 VARCHAR(64),
  technical_implementation_notes         TEXT,
  organizational_implementation_notes    TEXT,
  rating                                 BIGINT,
  rating_comment                         TEXT,
  search_words                           TEXT,

  FOREIGN KEY (rev) REFERENCES revisions(id),
  PRIMARY KEY (id, rev)
);

CREATE TABLE process_users_aud (
  rev                                    BIGINT NOT NULL,
  revtype                                TINYINT,

  user_id                                BIGINT,
  process_id                             BIGINT,

  FOREIGN KEY (rev) REFERENCES revisions(id),
  PRIMARY KEY (user_id, process_id, rev)
);

CREATE TABLE process_ous_aud (
  rev                                    BIGINT NOT NULL,
  revtype                                TINYINT,

  ou_id                                  BIGINT,
  process_id                             BIGINT,

  FOREIGN KEY (rev) REFERENCES revisions(id),
  PRIMARY KEY (ou_id, process_id, rev)
);

CREATE TABLE process_technologies_aud (
  rev                                    BIGINT NOT NULL,
  revtype                                TINYINT,

  technology_id                          BIGINT,
  process_id                             BIGINT,

  FOREIGN KEY (rev) REFERENCES revisions(id),
  PRIMARY KEY (technology_id, process_id, rev)
);

CREATE TABLE process_it_systems_aud (
  rev                                    BIGINT NOT NULL,
  revtype                                TINYINT,
  
  it_system_id                           BIGINT,
  process_id                             BIGINT,

  FOREIGN KEY (rev) REFERENCES revisions(id),
  PRIMARY KEY (it_system_id, process_id, rev)
);

CREATE TABLE process_links_aud (
  rev                                    BIGINT NOT NULL,
  revtype                                TINYINT,
  
  link_id                                BIGINT,
  process_id                             BIGINT,

  FOREIGN KEY (rev) REFERENCES revisions(id),
  PRIMARY KEY (link_id, process_id, rev)
);

CREATE TABLE process_children_aud (
  rev                                    BIGINT NOT NULL,
  revtype                                TINYINT,
  
  child_id                               BIGINT,
  process_id                             BIGINT,

  FOREIGN KEY (rev) REFERENCES revisions(id),
  PRIMARY KEY (child_id, process_id, rev)
);

CREATE TABLE process_domains_aud (
  rev                                    BIGINT NOT NULL,
  revtype                                TINYINT,

  process_id                             BIGINT,
  domain                                 VARCHAR(255),

  FOREIGN KEY (rev) REFERENCES revisions(id),
  PRIMARY KEY (process_id, domain, rev)
);
