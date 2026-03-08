-- =============================================================================
-- PURGA DE DATOS (PostgreSQL)
-- Ejecutar contra la base del proyecto para vaciar todas las tablas.
-- El esquema (tablas, índices, FKs) se mantiene; solo se borran los datos.
-- =============================================================================
-- Uso:
--   psql -h HOST -U USER -d DB -f purge-data.sql
--   o desde DBeaver/pgAdmin ejecutar este script.
-- =============================================================================

-- Desactivar triggers de FK temporalmente para poder truncar en cualquier orden
SET session_replication_role = replica;

-- Orden: primero tablas que referencian a otras (hijos), luego las raíz.
-- Así se evitan violaciones de FK al truncar.

TRUNCATE TABLE role_permissions;
TRUNCATE TABLE user_tags;
TRUNCATE TABLE client_billing_data;
TRUNCATE TABLE client_addresses;
TRUNCATE TABLE sale_lines;
TRUNCATE TABLE sale_history;
TRUNCATE TABLE quotation_lines;
TRUNCATE TABLE product_variant_options;
TRUNCATE TABLE product_variants;
TRUNCATE TABLE product_location_stock;
TRUNCATE TABLE product_prices;
TRUNCATE TABLE stock_movements;
TRUNCATE TABLE sales;
TRUNCATE TABLE quotations;
TRUNCATE TABLE products;
TRUNCATE TABLE clients;
TRUNCATE TABLE permissions;
TRUNCATE TABLE user_profiles;
TRUNCATE TABLE tags;
TRUNCATE TABLE price_lists;
TRUNCATE TABLE categories;
TRUNCATE TABLE brands;
TRUNCATE TABLE locations;

-- Reactivar triggers
SET session_replication_role = DEFAULT;

-- Opcional: reiniciar secuencias si en el futuro usas SERIAL/BIGSERIAL
-- SELECT setval(pg_get_serial_sequence(quote_ident(table_schema)||'.'||quote_ident(table_name), 'id'), 1) FROM information_schema.columns WHERE column_default LIKE 'nextval%' AND table_schema = 'public';
