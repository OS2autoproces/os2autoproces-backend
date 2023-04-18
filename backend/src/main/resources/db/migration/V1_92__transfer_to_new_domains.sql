UPDATE process_domains SET domain=CONCAT('OLD_',domain)
WHERE domain NOT LIKE 'OLD_%';

INSERT INTO process_domains (process_id, domain) SELECT process_id, 'EMPLOYMENT' FROM process_domains WHERE domain = 'OLD_WORK';
INSERT INTO process_domains (process_id, domain) SELECT process_id, 'PROFESSION' FROM process_domains WHERE domain = 'OLD_WORK';
INSERT INTO process_domains (process_id, domain) SELECT process_id, 'SOCIAL' FROM process_domains WHERE domain = 'OLD_HEALTH';
INSERT INTO process_domains (process_id, domain) SELECT process_id, 'HEALTH' FROM process_domains WHERE domain = 'OLD_HEALTH';
INSERT INTO process_domains (process_id, domain) SELECT process_id, 'FAMILY' FROM process_domains WHERE domain = 'OLD_CHILDREN';
INSERT INTO process_domains (process_id, domain) SELECT process_id, 'EDUCATION' FROM process_domains WHERE domain = 'OLD_CHILDREN';
INSERT INTO process_domains (process_id, domain) SELECT process_id, 'DEMOCRACY' FROM process_domains WHERE domain = 'OLD_DEMOCRACY';
INSERT INTO process_domains (process_id, domain) SELECT process_id, 'ENVIRONMENT' FROM process_domains WHERE domain = 'OLD_ENVIRONMENT';
INSERT INTO process_domains (process_id, domain) SELECT process_id, 'TECHNIQUE' FROM process_domains WHERE domain = 'OLD_ENVIRONMENT';
INSERT INTO process_domains (process_id, domain) SELECT process_id, 'ADMINISTRATION' FROM process_domains WHERE domain = 'OLD_ADMINISTRATION';


DELETE FROM process_domains WHERE domain = 'OLD_ADMINISTRATION' OR domain = 'OLD_WORK' OR domain = 'OLD_CHILDREN' OR domain = 'OLD_DEMOCRACY' OR domain = 'OLD_ENVIRONMENT' OR domain = 'OLD_HEALTH';