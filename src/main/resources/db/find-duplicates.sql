-- =============================================================================
-- DETECTAR DATOS DUPLICADOS (solo lectura)
-- Ejecuta cada bloque por separado o todo para ver un reporte.
-- =============================================================================

-- -----------------------
-- Listas de precios (mismo nombre ignorando mayúsculas/espacios)
-- -----------------------
SELECT 'price_lists' AS tabla, LOWER(TRIM(name)) AS clave, COUNT(*) AS cantidad, array_agg(id::text) AS ids
FROM price_lists
WHERE name IS NOT NULL AND TRIM(name) <> ''
GROUP BY LOWER(TRIM(name))
HAVING COUNT(*) > 1;

-- -----------------------
-- Marcas (mismo nombre)
-- -----------------------
SELECT 'brands' AS tabla, LOWER(TRIM(name)) AS clave, COUNT(*) AS cantidad, array_agg(id::text) AS ids
FROM brands
WHERE name IS NOT NULL AND TRIM(name) <> ''
GROUP BY LOWER(TRIM(name))
HAVING COUNT(*) > 1;

-- -----------------------
-- Ubicaciones (mismo nombre)
-- -----------------------
SELECT 'locations' AS tabla, LOWER(TRIM(name)) AS clave, COUNT(*) AS cantidad, array_agg(id::text) AS ids
FROM locations
WHERE name IS NOT NULL AND TRIM(name) <> ''
GROUP BY LOWER(TRIM(name))
HAVING COUNT(*) > 1;

-- -----------------------
-- Categorías (mismo nombre)
-- -----------------------
SELECT 'categories' AS tabla, LOWER(TRIM(name)) AS clave, COUNT(*) AS cantidad, array_agg(id::text) AS ids
FROM categories
WHERE name IS NOT NULL AND TRIM(name) <> ''
GROUP BY LOWER(TRIM(name))
HAVING COUNT(*) > 1;

-- -----------------------
-- Productos por SKU
-- -----------------------
SELECT 'products (sku)' AS tabla, LOWER(TRIM(sku)) AS clave, COUNT(*) AS cantidad, array_agg(id::text) AS ids
FROM products
WHERE sku IS NOT NULL AND TRIM(sku) <> ''
GROUP BY LOWER(TRIM(sku))
HAVING COUNT(*) > 1;

-- -----------------------
-- Productos por código de barras
-- -----------------------
SELECT 'products (barcode)' AS tabla, LOWER(TRIM(barcode)) AS clave, COUNT(*) AS cantidad, array_agg(id::text) AS ids
FROM products
WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
GROUP BY LOWER(TRIM(barcode))
HAVING COUNT(*) > 1;

-- -----------------------
-- Productos por (nombre + marca) cuando no hay SKU/barcode único
-- -----------------------
SELECT 'products (name+brand)' AS tabla,
       LOWER(TRIM(p.name)) || ' | ' || COALESCE(p.brand_id::text, 'sin marca') AS clave,
       COUNT(*) AS cantidad,
       array_agg(p.id::text) AS ids
FROM products p
WHERE p.name IS NOT NULL AND TRIM(p.name) <> ''
GROUP BY LOWER(TRIM(p.name)), COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
HAVING COUNT(*) > 1;

-- -----------------------
-- Clientes por email (posibles duplicados)
-- -----------------------
SELECT 'clients (email)' AS tabla, LOWER(TRIM(email)) AS clave, COUNT(*) AS cantidad, array_agg(id::text) AS ids
FROM clients
WHERE email IS NOT NULL AND TRIM(email) <> ''
GROUP BY LOWER(TRIM(email))
HAVING COUNT(*) > 1;

-- -----------------------
-- Resumen: total de filas duplicadas por tipo
-- -----------------------
SELECT 'RESUMEN' AS info, (
    (SELECT COUNT(*) FROM (SELECT 1 FROM price_lists WHERE name IS NOT NULL AND TRIM(name) <> '' GROUP BY LOWER(TRIM(name)) HAVING COUNT(*) > 1) x) +
    (SELECT COUNT(*) FROM (SELECT 1 FROM brands WHERE name IS NOT NULL AND TRIM(name) <> '' GROUP BY LOWER(TRIM(name)) HAVING COUNT(*) > 1) x) +
    (SELECT COUNT(*) FROM (SELECT 1 FROM locations WHERE name IS NOT NULL AND TRIM(name) <> '' GROUP BY LOWER(TRIM(name)) HAVING COUNT(*) > 1) x) +
    (SELECT COUNT(*) FROM (SELECT 1 FROM categories WHERE name IS NOT NULL AND TRIM(name) <> '' GROUP BY LOWER(TRIM(name)) HAVING COUNT(*) > 1) x) +
    (SELECT COUNT(*) FROM (SELECT 1 FROM products WHERE sku IS NOT NULL AND TRIM(sku) <> '' GROUP BY LOWER(TRIM(sku)) HAVING COUNT(*) > 1) x) +
    (SELECT COUNT(*) FROM (SELECT 1 FROM products WHERE barcode IS NOT NULL AND TRIM(barcode) <> '' GROUP BY LOWER(TRIM(barcode)) HAVING COUNT(*) > 1) x) +
    (SELECT COUNT(*) FROM (
        SELECT 1 FROM products p WHERE p.name IS NOT NULL AND TRIM(p.name) <> ''
        GROUP BY LOWER(TRIM(p.name)), COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
        HAVING COUNT(*) > 1
    ) x) +
    (SELECT COUNT(*) FROM (SELECT 1 FROM clients WHERE email IS NOT NULL AND TRIM(email) <> '' GROUP BY LOWER(TRIM(email)) HAVING COUNT(*) > 1) x)
) AS grupos_duplicados;
