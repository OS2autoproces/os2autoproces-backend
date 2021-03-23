CREATE TABLE kle (
  code                VARCHAR(8) NOT NULL PRIMARY KEY,
  name                VARCHAR(255) NOT NULL,

  UNIQUE(code)
);

CREATE TABLE form (
  code                VARCHAR(11) NOT NULL PRIMARY KEY,
  description         VARCHAR(255) NOT NULL,

  UNIQUE(code)
);

CREATE TABLE kle_forms (
  kle_code            VARCHAR(8) NOT NULL,
  form_code           VARCHAR(11) NOT NULL,
	
  FOREIGN KEY (kle_code) REFERENCES kle(code) ON DELETE CASCADE,
  FOREIGN KEY (form_code) REFERENCES form(code) ON DELETE CASCADE
)
