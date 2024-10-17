ALTER TABLE municipalities
   ADD inhabitants VARCHAR(36) NULL,
   ADD employees VARCHAR(36) NULL,
   ADD auto_other_contact_email VARCHAR(255) NULL,
   ADD local_admin BIGINT NULL;

ALTER TABLE municipalities ADD CONSTRAINT fk_municipalities_local_admin FOREIGN KEY (local_admin) REFERENCES users (id);

CREATE TABLE logos (
   id BIGINT AUTO_INCREMENT NOT NULL,
   file_name VARCHAR(255) NOT NULL,
   url VARCHAR(255) NOT NULL,
   municipality_id BIGINT NOT NULL,
   CONSTRAINT pk_logos PRIMARY KEY (id),
   CONSTRAINT fk_logos_municipality FOREIGN KEY (municipality_id) REFERENCES municipalities (id),
   CONSTRAINT uc_logos_municipality UNIQUE (municipality_id)
);

CREATE TABLE municipality_technologies (
  id                                     BIGINT AUTO_INCREMENT NOT NULL,
  technology_id                          BIGINT NOT NULL,
  municipality_id                        BIGINT NOT NULL,

  CONSTRAINT pk_municipality_technologies PRIMARY KEY (id),
  CONSTRAINT fk_municipality_technologies_technology FOREIGN KEY (technology_id) REFERENCES technologies(id) ON DELETE CASCADE,
  CONSTRAINT fk_municipality_technologies_municipality FOREIGN KEY (municipality_id) REFERENCES municipalities(id) ON DELETE CASCADE
);

CREATE TABLE municipality_it_systems (
  id                                     BIGINT AUTO_INCREMENT NOT NULL,
  it_system_id                           BIGINT NOT NULL,
  municipality_id                        BIGINT NOT NULL,

  CONSTRAINT pk_municipality_it_systems PRIMARY KEY (id),
  CONSTRAINT fk_municipality_it_systems_it_system FOREIGN KEY (it_system_id) REFERENCES it_systems(id) ON DELETE CASCADE,
  CONSTRAINT fk_municipality_it_systems_municipality FOREIGN KEY (municipality_id) REFERENCES municipalities(id) ON DELETE CASCADE
);