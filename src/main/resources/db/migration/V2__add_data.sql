-- 1. Insert Products (Chicken related)
INSERT INTO product (name, description, price, status, quantity_on_hand)
VALUES ('Whole Chicken', 'Fresh organic whole chicken, 1.5kg', 150.00, 'IN_STOCK', 50),
       ('Chicken Wings (1kg)', 'Spicy marinated chicken wings', 99.50, 'IN_STOCK', 100),
       ('Free Range Eggs (12pk)', 'Large brown eggs from happy hens', 45.00, 'IN_STOCK', 200),
       ('Chicken Breast Fillet', 'Boneless skinless breast fillets', 180.00, 'OUT_OF_STOCK', 0);

-- 2. Insert Customers
INSERT INTO customer (name, phone, email)
VALUES ('Ola Nordmann', '12345678', 'ola@example.com'),
       ('Kari Olsen', '98765432', 'kari@example.com');

-- 3. Insert Addresses using customer emails instead of hard-coded IDs
INSERT INTO address (street, city, postal_code, country, customer_id)
VALUES ('Karl Johans gate 1', 'Oslo', '0154', 'Norway',
        (SELECT id FROM customer WHERE email = 'ola@example.com')),
       ('Storgata 10', 'Bergen', '5000', 'Norway',
        (SELECT id FROM customer WHERE email = 'kari@example.com')),
       ('Feriehuset vei 5', 'Hemsedal', '3560', 'Norway',
        (SELECT id FROM customer WHERE email = 'ola@example.com'));

-- 4. Insert a Past Order for Ola - denormalized shipping address
INSERT INTO orders (total_price, shipping_charge, is_shipped, customer_id, shipping_street, shipping_city, shipping_postal_code, shipping_country, creation_date)
VALUES (249.50,
        50.00,
        true,
        (SELECT id FROM customer WHERE email = 'ola@example.com'),
        'Karl Johans gate 1',
        'Oslo',
        '0154',
        'Norway',
        '2024-11-01 10:00:00');

-- 5. Insert Order Items for that Order
INSERT INTO order_item (quantity, order_id, product_id)
VALUES (1,
        (SELECT id FROM orders ORDER BY id ASC LIMIT 1),
        (SELECT id FROM product WHERE name = 'Whole Chicken')),
       (1,
        (SELECT id FROM orders ORDER BY id ASC LIMIT 1),
        (SELECT id FROM product WHERE name = 'Chicken Wings (1kg)'));
