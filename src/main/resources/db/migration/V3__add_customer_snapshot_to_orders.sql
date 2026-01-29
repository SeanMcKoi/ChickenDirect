ALTER TABLE orders
ADD COLUMN customer_name VARCHAR(255),
ADD COLUMN customer_email VARCHAR(255),
ADD COLUMN customer_phone VARCHAR(255);

UPDATE orders o
SET customer_name = c.name,
    customer_email = c.email,
    customer_phone = c.phone
FROM customer c
WHERE o.customer_id = c.id;
