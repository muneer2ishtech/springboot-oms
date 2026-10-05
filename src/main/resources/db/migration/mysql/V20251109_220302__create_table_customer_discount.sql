CREATE TABLE t_customer_discount (
  id                 BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT PRIMARY KEY,
  customer_id        BIGINT           NOT NULL,
  product_id         BIGINT UNSIGNED      NULL,
  discount_type      ENUM('SALES_ORDER_PERCENT', 'PRODUCT_PERCENT', 'PRODUCT_BUY_X_PAY_Y') NOT NULL,
  discount_percent   DECIMAL(5, 4)        NULL,
  buy_quantity       INT                  NULL,
  pay_quantity       INT                  NULL,
  is_active          BIT              NOT NULL DEFAULT b'1',
  description        TEXT                 NULL
);

ALTER TABLE t_customer_discount
  ADD CONSTRAINT uk_customer_discount_customer_id_discount_type_product_id
  UNIQUE (customer_id, discount_type, product_id)
;

ALTER TABLE t_customer_discount
  ADD CONSTRAINT fk_customer_discount_customer_id_user_profile
  FOREIGN KEY (customer_id)
  REFERENCES t_user_profile(id)
;

ALTER TABLE t_customer_discount
  ADD CONSTRAINT fk_customer_discount_product
  FOREIGN KEY (product_id)
  REFERENCES t_product(id)
;
