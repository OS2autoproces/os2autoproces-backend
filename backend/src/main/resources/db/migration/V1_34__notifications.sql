CREATE TABLE notifications (
  id                                     BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  user_id                                BIGINT NOT NULL,
  process_id                             BIGINT NOT NULL,

  UNIQUE(user_id, process_id),

  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (process_id) REFERENCES process(id) ON DELETE CASCADE
);
