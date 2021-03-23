CREATE TABLE identity_provider (
  id                              BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name                            VARCHAR(64) NOT NULL,
  entity_id                       VARCHAR(255) NOT NULL,
  cvr                             VARCHAR(8),
  metadata                        TEXT
);