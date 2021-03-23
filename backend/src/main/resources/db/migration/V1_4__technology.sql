CREATE TABLE technologies (
    id                  BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name                VARCHAR(255) NOT NULL,
    
    UNIQUE(name)
);
