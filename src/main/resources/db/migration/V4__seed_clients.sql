-- Listas de precios
INSERT INTO price_lists (id, name, description) VALUES
('a1000000-0000-0000-0000-000000000001', 'Público general', 'Precios estándar'),
('a1000000-0000-0000-0000-000000000002', 'Mayoreo', 'Clientes mayoreo y distribuidores'),
('a1000000-0000-0000-0000-000000000003', 'Proveedores', 'Precio especial proveedores');

-- Clientes de ejemplo (ventas de proteínas, uso interno)
INSERT INTO clients (id, name, last_name, phone, email, comments, price_list_id, credit_limit, client_type) VALUES
('b2000000-0000-0000-0000-000000000001', 'Alexander', 'Stum Comple May', '+525548294866', 'munchalec@gmail.com', 'Cliente de Mayoreo', 'a1000000-0000-0000-0000-000000000002', NULL, 'CONSUMER'),
('b2000000-0000-0000-0000-000000000002', 'María', 'García López', '+525512345678', 'maria.garcia@email.com', NULL, 'a1000000-0000-0000-0000-000000000001', 50000.00, 'CONSUMER'),
('b2000000-0000-0000-0000-000000000003', 'Proteínas del Centro', NULL, '+525578901234', 'ventas@proteinascentro.mx', 'Distribuidor zona centro', 'a1000000-0000-0000-0000-000000000003', 200000.00, 'DISTRIBUTOR');

-- Dirección principal (Alexander - como en el ejemplo)
INSERT INTO client_addresses (id, client_id, address_type, street, exterior_number, interior_number, postal_code, colonia, municipio, city, state, country) VALUES
('c3000000-0000-0000-0000-000000000001', 'b2000000-0000-0000-0000-000000000001', 'MAIN', 'Nicolas Bravo', '98', NULL, '55606', 'Barrio San Marcos', 'Zupango', 'Zupango', 'Estado de México', 'México'),
('c3000000-0000-0000-0000-000000000002', 'b2000000-0000-0000-0000-000000000002', 'MAIN', 'Av. Insurgentes', '1234', 'B', '03100', 'Del Valle', 'Benito Juárez', 'Ciudad de México', 'CDMX', 'México'),
('c3000000-0000-0000-0000-000000000003', 'b2000000-0000-0000-0000-000000000003', 'MAIN', 'Calle Hidalgo', '500', NULL, '76000', 'Centro', 'Querétaro', 'Querétaro', 'Querétaro', 'México');

-- Datos de facturación (solo para el que los tenga en el sistema actual; Alexander "Sin Datos de Facturación" en el ejemplo, agregamos uno con datos)
INSERT INTO client_billing_data (id, client_id, razon_social, postal_code, rfc, regimen_fiscal) VALUES
('d4000000-0000-0000-0000-000000000001', 'b2000000-0000-0000-0000-000000000003', 'Proteínas del Centro S.A. de C.V.', '76000', 'PCE850101ABC', '601 - General de Ley Personas Morales');

-- Ventas de ejemplo (Alexander: 5 ventas, total $6,420, deuda $0 - como en tu pantalla)
INSERT INTO sales (id, sale_number, client_id, total, currency, payment_status, delivery_status, sale_date) VALUES
('e5000000-0000-0000-0000-000000000001', 491, 'b2000000-0000-0000-0000-000000000001', 1565.00, 'MXN', 'PAID', 'DELIVERED', '2026-01-12 10:22:53'),
('e5000000-0000-0000-0000-000000000002', 412, 'b2000000-0000-0000-0000-000000000001', 1710.00, 'MXN', 'PAID', 'DELIVERED', '2025-12-03 19:50:29'),
('e5000000-0000-0000-0000-000000000003', 344, 'b2000000-0000-0000-0000-000000000001', 610.00, 'MXN', 'PAID', 'DELIVERED', '2025-11-05 15:03:23'),
('e5000000-0000-0000-0000-000000000004', 341, 'b2000000-0000-0000-0000-000000000001', 1210.00, 'MXN', 'PAID', 'DELIVERED', '2025-11-04 15:00:09'),
('e5000000-0000-0000-0000-000000000005', 311, 'b2000000-0000-0000-0000-000000000001', 1325.00, 'MXN', 'PAID', 'DELIVERED', '2025-10-21 10:15:15'),
('e5000000-0000-0000-0000-000000000006', 490, 'b2000000-0000-0000-0000-000000000002', 3200.00, 'MXN', 'PAID', 'DELIVERED', '2026-01-10 09:00:00'),
('e5000000-0000-0000-0000-000000000007', 488, 'b2000000-0000-0000-0000-000000000002', 1500.00, 'MXN', 'PENDING', 'PENDING', '2026-02-01 14:30:00'),
('e5000000-0000-0000-0000-000000000008', 492, 'b2000000-0000-0000-0000-000000000003', 45000.00, 'MXN', 'PAID', 'PENDING', '2026-02-13 11:00:00');
