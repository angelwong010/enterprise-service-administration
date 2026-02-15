-- Ubicación por defecto
INSERT INTO locations (id, name) VALUES
('f6000000-0000-0000-0000-000000000001', 'Sucursal Principal');

-- Categorías (como en tu listado)
INSERT INTO categories (id, name) VALUES
('f7000000-0000-0000-0000-000000000001', 'Quemadores'),
('f7000000-0000-0000-0000-000000000002', 'Farmacia'),
('f7000000-0000-0000-0000-000000000003', 'Mesoterapia'),
('f7000000-0000-0000-0000-000000000004', 'Suplementos'),
('f7000000-0000-0000-0000-000000000005', 'Anastrozol'),
('f7000000-0000-0000-0000-000000000006', 'Boldenona'),
('f7000000-0000-0000-0000-000000000007', 'Somatropina');

-- Marcas
INSERT INTO brands (id, name) VALUES
('f8000000-0000-0000-0000-000000000001', 'Mesofrance'),
('f8000000-0000-0000-0000-000000000002', 'Dermatti'),
('f8000000-0000-0000-0000-000000000003', 'Alpha Pharma'),
('f8000000-0000-0000-0000-000000000004', 'Thaiger Pharma');

-- Producto ejemplo: Acelerador Metabolico Mesofrance (como en tu detalle)
INSERT INTO products (id, name, sku, barcode, product_type, category_id, brand_id, unit_of_sale, location_text, description, use_stock, use_lots_expiry, charge_tax, iva_rate, ieps_rate, cost_net, cost_with_tax, include_in_catalog, sell_at_pos, require_prescription, allow_manufacturing, sat_key) VALUES
('f9000000-0000-0000-0000-000000000001', 'Acelerador Metabolico Mesofrance', 'ACM-MES-01', NULL, 'PRODUCT', 'f7000000-0000-0000-0000-000000000001', 'f8000000-0000-0000-0000-000000000001', 'Capsulas', NULL, 'Acelerador Metabolico Capsulas marca Mesofrance', true, false, true, 16.00, 0, 250.00, 250.00, true, true, false, false, NULL);

-- Precios por lista (Público, Mayoreo, Super Mayoreo, VIP - IDs 1,2,4,5). Sin id explícito: usa gen_random_uuid().
INSERT INTO product_prices (product_id, price_list_id, price, currency) VALUES
('f9000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000001', 570.00, 'MXN'),
('f9000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000002', 390.00, 'MXN'),
('f9000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000004', 340.00, 'MXN'),
('f9000000-0000-0000-0000-000000000001', 'a1000000-0000-0000-0000-000000000005', 310.00, 'MXN');

-- Stock en Sucursal Principal: 7 unidades, mínimo 5
INSERT INTO product_location_stock (product_id, location_id, quantity, min_quantity) VALUES
('f9000000-0000-0000-0000-000000000001', 'f6000000-0000-0000-0000-000000000001', 7, 5);

-- Últimos movimientos (como en tu pantalla)
INSERT INTO stock_movements (product_id, location_id, movement_type, quantity, previous_quantity, new_quantity, reference_type, reference_id, user_id, created_at) VALUES
('f9000000-0000-0000-0000-000000000001', 'f6000000-0000-0000-0000-000000000001', 'OUT', -2, 9, 7, 'SALE', NULL, NULL, '2026-01-21 19:05:30'),
('f9000000-0000-0000-0000-000000000001', 'f6000000-0000-0000-0000-000000000001', 'OUT', -1, 10, 9, 'SALE', NULL, NULL, '2025-09-25 15:50:17'),
('f9000000-0000-0000-0000-000000000001', 'f6000000-0000-0000-0000-000000000001', 'IN', 10, 0, 10, 'CREATION', NULL, NULL, '2025-02-26 14:51:04');

-- Más productos de ejemplo
INSERT INTO products (id, name, sku, product_type, category_id, brand_id, unit_of_sale, description, use_stock, charge_tax, iva_rate, cost_net, include_in_catalog, sell_at_pos) VALUES
('f9000000-0000-0000-0000-000000000002', 'Acxion Fentarmina', 'ACX-01', 'PRODUCT', 'f7000000-0000-0000-0000-000000000002', NULL, 'unidades', NULL, true, true, 16, 200.00, true, true),
('f9000000-0000-0000-0000-000000000003', 'Alessandria Coctel Lipoescultor Dermatti', 'ALC-DER-01', 'PRODUCT', 'f7000000-0000-0000-0000-000000000003', 'f8000000-0000-0000-0000-000000000002', 'unidades', NULL, true, true, 16, 250.00, true, true),
('f9000000-0000-0000-0000-000000000004', 'Amino Energy 30 Servicios', 'AME-30', 'PRODUCT', 'f7000000-0000-0000-0000-000000000004', NULL, 'unidades', NULL, true, true, 16, 280.00, true, true),
('f9000000-0000-0000-0000-000000000005', 'Anastrozol 30 Tabs Alpha Pharma', 'ANA-AP-30', 'PRODUCT', 'f7000000-0000-0000-0000-000000000005', 'f8000000-0000-0000-0000-000000000003', 'unidades', NULL, true, true, 16, 800.00, true, true);

INSERT INTO product_prices (product_id, price_list_id, price, currency) VALUES
('f9000000-0000-0000-0000-000000000002', 'a1000000-0000-0000-0000-000000000001', 370.00, 'MXN'),
('f9000000-0000-0000-0000-000000000003', 'a1000000-0000-0000-0000-000000000001', 450.00, 'MXN'),
('f9000000-0000-0000-0000-000000000004', 'a1000000-0000-0000-0000-000000000001', 480.00, 'MXN'),
('f9000000-0000-0000-0000-000000000005', 'a1000000-0000-0000-0000-000000000001', 1330.00, 'MXN');

INSERT INTO product_location_stock (product_id, location_id, quantity, min_quantity) VALUES
('f9000000-0000-0000-0000-000000000002', 'f6000000-0000-0000-0000-000000000001', 50, 0),
('f9000000-0000-0000-0000-000000000003', 'f6000000-0000-0000-0000-000000000001', 8, 0),
('f9000000-0000-0000-0000-000000000004', 'f6000000-0000-0000-0000-000000000001', 7, 0),
('f9000000-0000-0000-0000-000000000005', 'f6000000-0000-0000-0000-000000000001', 9, 0);
