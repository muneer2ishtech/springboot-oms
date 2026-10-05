CREATE TABLE t_product (
  id           BIGINT UNSIGNED  NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name         VARCHAR(255)     NOT NULL,
  unit_price   DECIMAL(15, 2)   NOT NULL,
  is_active    BIT              NOT NULL DEFAULT b'1',
  description  TEXT                 NULL
);

ALTER TABLE t_product
  ADD CONSTRAINT uk_product_name
  UNIQUE (name)
;
