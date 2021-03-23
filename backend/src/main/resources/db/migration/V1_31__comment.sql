CREATE TABLE comments (
  id                                     BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  message                                TEXT NOT NULL,
  name                                   VARCHAR(255) NOT NULL,
  created                                TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  process_id                             BIGINT NOT NULL,

  FOREIGN KEY (process_id) REFERENCES process(id) ON DELETE CASCADE
);