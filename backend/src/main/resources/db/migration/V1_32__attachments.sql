CREATE TABLE attachments (
  id                                     BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  file_name                              VARCHAR(255) NOT NULL,
  url                                    VARCHAR(255) NOT NULL,
  visible_to_other_municipalities        BOOLEAN NOT NULL,
  process_id                             BIGINT NOT NULL,

  FOREIGN KEY (process_id) REFERENCES process(id) ON DELETE CASCADE
);
