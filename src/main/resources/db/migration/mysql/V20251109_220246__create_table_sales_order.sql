CREATE TABLE t_sales_order (
  id                 BIGINT UNSIGNED  NOT NULL PRIMARY KEY AUTO_INCREMENT,
  customer_id        BIGINT           NOT NULL,
  orig_total_amount  DECIMAL(15, 2)   NOT NULL,
  total_amount       DECIMAL(15, 2)   NOT NULL,
  discount_percent   DECIMAL(5, 4)        NULL,
  discount_amount    DECIMAL(15, 2)       NULL,
  net_amount         DECIMAL(15, 2)   NOT NULL,
  is_active          BIT              NOT NULL DEFAULT b'1',
  description        TEXT                 NULL
);

ALTER TABLE t_sales_order
  ADD CONSTRAINT fk_sales_order_customer_id_user_profile
  FOREIGN KEY (customer_id)
  REFERENCES t_user_profile(id)
;
