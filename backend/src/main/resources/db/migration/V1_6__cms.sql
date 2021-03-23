CREATE TABLE cms_entries (
  id                              BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  label                           VARCHAR(64) NOT NULL,
  content                         TEXT NOT NULL,

  UNIQUE(label)
);

INSERT INTO cms_entries (label, content) VALUES ("frontPageText", "Please change this text");