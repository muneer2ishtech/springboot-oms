DROP DATABASE IF EXISTS `ishtech_oms_dev_db`;
CREATE DATABASE IF NOT EXISTS `ishtech_oms_dev_db`
  DEFAULT CHARACTER SET = utf8mb4
  DEFAULT COLLATE = utf8mb4_unicode_ci
;

DROP DATABASE IF EXISTS `ishtech_oms_dev_aud_db`;
CREATE DATABASE IF NOT EXISTS `ishtech_oms_dev_aud_db`
  DEFAULT CHARACTER SET = utf8mb4
  DEFAULT COLLATE = utf8mb4_unicode_ci
;

DROP USER IF EXISTS `ishtech_oms_dev_user`@`%`;
CREATE USER IF NOT EXISTS `ishtech_oms_dev_user`@`%`
  IDENTIFIED BY 'ishtech_oms_dev_pass'
;

DROP USER IF EXISTS `ishtech_oms_dev_flyway_user`@`%`;
CREATE USER IF NOT EXISTS `ishtech_oms_dev_flyway_user`@`%`
  IDENTIFIED BY 'ishtech_oms_dev_flyway_pass'
;

GRANT SELECT, INSERT, UPDATE ON `ishtech_oms_dev_db`.* TO `ishtech_oms_dev_user`@`%`;
GRANT DELETE ON `ishtech_oms_dev_db`.* TO `ishtech_oms_dev_user`@`%`;
GRANT SELECT, INSERT ON `ishtech_oms_dev_aud_db`.* TO `ishtech_oms_dev_user`@`%`;

FLUSH PRIVILEGES;

GRANT CREATE, REFERENCES, INDEX, ALTER, LOCK TABLES ON `ishtech_oms_dev_db`.* TO `ishtech_oms_dev_flyway_user`@`%`;
GRANT SELECT, INSERT, UPDATE ON `ishtech_oms_dev_db`.* TO `ishtech_oms_dev_flyway_user`@`%`;
-- GRANT DELETE ON `ishtech_oms_dev_db`.* TO `ishtech_oms_dev_flyway_user`@`%`;

GRANT CREATE, REFERENCES, INDEX, ALTER, LOCK TABLES ON `ishtech_oms_dev_aud_db`.* TO `ishtech_oms_dev_flyway_user`@`%`;
GRANT SELECT ON `ishtech_oms_dev_aud_db`.* TO `ishtech_oms_dev_flyway_user`@`%`;

FLUSH PRIVILEGES;

SHOW GRANTS FOR `ishtech_oms_dev_user`@`%`;
SHOW GRANTS FOR `ishtech_oms_dev_flyway_user`@`%`;
