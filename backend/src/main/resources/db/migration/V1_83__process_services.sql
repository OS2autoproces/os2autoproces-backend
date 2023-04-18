CREATE TABLE service (
  id                                     BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name                                   VARCHAR(255) NOT NULL
);

CREATE TABLE process_services (
  service_id                             BIGINT NOT NULL,
  process_id                             BIGINT NOT NULL,

  FOREIGN KEY (process_id) REFERENCES process(id) ON DELETE CASCADE,
  FOREIGN KEY (service_id) REFERENCES service(id) ON DELETE CASCADE
);

CREATE TABLE process_services_aud (
  rev                                    BIGINT NOT NULL,
  revtype                                TINYINT,

  service_id                             BIGINT,
  process_id                             BIGINT,

  FOREIGN KEY (rev) REFERENCES revisions(id)
);