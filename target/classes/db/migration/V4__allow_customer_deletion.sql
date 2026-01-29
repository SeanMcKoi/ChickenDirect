ALTER TABLE orders
DROP CONSTRAINT orders_customer_id_fkey;

ALTER TABLE orders
ADD CONSTRAINT orders_customer_id_fkey
FOREIGN KEY (customer_id)
REFERENCES customer (id)
ON DELETE SET NULL;
