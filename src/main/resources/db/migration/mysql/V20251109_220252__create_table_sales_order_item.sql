CREATE TABLE t_sales_order_item (
  id                 BIGINT UNSIGNED  NOT NULL AUTO_INCREMENT PRIMARY KEY,
  sales_order_id     BIGINT UNSIGNED  NOT NULL,
  product_id         BIGINT UNSIGNED  NOT NULL,
  quantity           INT              NOT NULL,
  unit_price         DECIMAL(10, 2)   NOT NULL,
  orig_line_amount   DECIMAL(10, 2)   NOT NULL,
  discount_percent   DECIMAL(5, 4)        NULL,
  discount_amount    DECIMAL(15, 2)       NULL,
  line_amount        DECIMAL(15, 2)   NOT NULL,
  is_active          BIT              NOT NULL DEFAULT b'1',
  description        TEXT                 NULL
);

ALTER TABLE t_sales_order_item
  ADD CONSTRAINT uk_sales_order_item_sales_order_id_product_id
  UNIQUE (sales_order_id, product_id)
;

ALTER TABLE t_sales_order_item
  ADD CONSTRAINT fk_sales_order_item_sales_order
  FOREIGN KEY (sales_order_id)
  REFERENCES t_sales_order(id)
;

ALTER TABLE t_sales_order_item
  ADD CONSTRAINT fk_sales_order_item_product
  FOREIGN KEY (product_id)
  REFERENCES t_product(id)
;
