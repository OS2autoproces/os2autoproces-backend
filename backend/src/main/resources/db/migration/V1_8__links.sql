CREATE TABLE link (
    id                  BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    url                 VARCHAR(1024) NOT NULL,
    internal            BOOLEAN NOT NULL
);
