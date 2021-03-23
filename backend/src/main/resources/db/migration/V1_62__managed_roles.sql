CREATE TABLE administrator (
  id                   BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  user_uuid            VARCHAR(36) NOT NULL,
  role                 VARCHAR(128) NOT NULL
);