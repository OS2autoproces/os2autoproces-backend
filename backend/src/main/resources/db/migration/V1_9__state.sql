CREATE TABLE states (
    id                  BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    state_key           VARCHAR(64) NOT NULL,
    state_value         VARCHAR(1024) NOT NULL
);
