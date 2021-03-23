CREATE TABLE municipalities (
  id                              BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name                            VARCHAR(64) NOT NULL,
  cvr                             VARCHAR(8) NOT NULL,
  api_key                         VARCHAR(36)
);
