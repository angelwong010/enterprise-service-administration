-- =============================================================================
-- ELIMINAR DATOS DUPLICADOS
-- Misma lógica que las migraciones V11, V12, V13: se conserva un registro por
-- grupo (el de id mínimo) y se reasignan las FKs; luego se borran los duplicados.
-- =============================================================================
-- Antes de ejecutar:
--   1. Ejecuta find-duplicates.sql para ver qué hay duplicado.
--   2. Haz backup de la base (pg_dump o tu herramienta).
--   3. Ejecuta este script en una transacción; si algo falla, haz ROLLBACK.
-- =============================================================================

BEGIN;

-- -----------------------
-- 1) PRICE LISTS (mismo nombre)
-- -----------------------
WITH pl_keep AS (
    SELECT LOWER(TRIM(name)) AS k, MIN(id::text)::uuid AS keep_id
    FROM price_lists
    WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name))
),
pl_dupes AS (
    SELECT pl.id AS dup_id, k.keep_id
    FROM price_lists pl
    JOIN pl_keep k ON LOWER(TRIM(pl.name)) = k.k
    WHERE pl.id <> k.keep_id
)
UPDATE clients c SET price_list_id = d.keep_id
FROM pl_dupes d WHERE c.price_list_id = d.dup_id;

WITH pl_keep AS (
    SELECT LOWER(TRIM(name)) AS k, MIN(id::text)::uuid AS keep_id
    FROM price_lists WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name))
),
pl_dupes AS (
    SELECT pl.id AS dup_id, k.keep_id
    FROM price_lists pl JOIN pl_keep k ON LOWER(TRIM(pl.name)) = k.k
    WHERE pl.id <> k.keep_id
)
DELETE FROM product_prices pp
USING pl_dupes d WHERE pp.price_list_id = d.dup_id
  AND EXISTS (SELECT 1 FROM product_prices pp2 WHERE pp2.product_id = pp.product_id AND pp2.price_list_id = d.keep_id);

WITH pl_keep AS (
    SELECT LOWER(TRIM(name)) AS k, MIN(id::text)::uuid AS keep_id
    FROM price_lists WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name))
),
pl_dupes AS (
    SELECT pl.id AS dup_id, k.keep_id
    FROM price_lists pl JOIN pl_keep k ON LOWER(TRIM(pl.name)) = k.k
    WHERE pl.id <> k.keep_id
)
UPDATE product_prices pp SET price_list_id = d.keep_id
FROM pl_dupes d WHERE pp.price_list_id = d.dup_id;

WITH pl_keep AS (
    SELECT LOWER(TRIM(name)) AS k, MIN(id::text)::uuid AS keep_id
    FROM price_lists WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name))
),
pl_dupes AS (
    SELECT pl.id AS dup_id, k.keep_id
    FROM price_lists pl JOIN pl_keep k ON LOWER(TRIM(pl.name)) = k.k
    WHERE pl.id <> k.keep_id
)
DELETE FROM price_lists pl USING pl_dupes d WHERE pl.id = d.dup_id;

-- -----------------------
-- 2) BRANDS (mismo nombre)
-- -----------------------
WITH b_keep AS (
    SELECT LOWER(TRIM(name)) AS k, MIN(id::text)::uuid AS keep_id
    FROM brands WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name))
),
b_dupes AS (
    SELECT b.id AS dup_id, k.keep_id
    FROM brands b JOIN b_keep k ON LOWER(TRIM(b.name)) = k.k
    WHERE b.id <> k.keep_id
)
UPDATE products p SET brand_id = d.keep_id
FROM b_dupes d WHERE p.brand_id = d.dup_id;

WITH b_keep AS (
    SELECT LOWER(TRIM(name)) AS k, MIN(id::text)::uuid AS keep_id
    FROM brands WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name))
),
b_dupes AS (
    SELECT b.id AS dup_id, k.keep_id
    FROM brands b JOIN b_keep k ON LOWER(TRIM(b.name)) = k.k
    WHERE b.id <> k.keep_id
)
DELETE FROM brands b USING b_dupes d WHERE b.id = d.dup_id;

-- -----------------------
-- 3) LOCATIONS (mismo nombre)
-- -----------------------
WITH l_keep AS (
    SELECT LOWER(TRIM(name)) AS k, MIN(id::text)::uuid AS keep_id
    FROM locations WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name))
),
l_dupes AS (
    SELECT l.id AS dup_id, k.keep_id
    FROM locations l JOIN l_keep k ON LOWER(TRIM(l.name)) = k.k
    WHERE l.id <> k.keep_id
)
DELETE FROM product_location_stock pls
USING l_dupes d WHERE pls.location_id = d.dup_id
  AND EXISTS (SELECT 1 FROM product_location_stock pls2 WHERE pls2.product_id = pls.product_id AND pls2.location_id = d.keep_id);

WITH l_keep AS (
    SELECT LOWER(TRIM(name)) AS k, MIN(id::text)::uuid AS keep_id
    FROM locations WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name))
),
l_dupes AS (
    SELECT l.id AS dup_id, k.keep_id
    FROM locations l JOIN l_keep k ON LOWER(TRIM(l.name)) = k.k
    WHERE l.id <> k.keep_id
)
UPDATE product_location_stock pls SET location_id = d.keep_id
FROM l_dupes d WHERE pls.location_id = d.dup_id;

WITH l_keep AS (
    SELECT LOWER(TRIM(name)) AS k, MIN(id::text)::uuid AS keep_id
    FROM locations WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name))
),
l_dupes AS (
    SELECT l.id AS dup_id, k.keep_id
    FROM locations l JOIN l_keep k ON LOWER(TRIM(l.name)) = k.k
    WHERE l.id <> k.keep_id
)
UPDATE stock_movements sm SET location_id = d.keep_id
FROM l_dupes d WHERE sm.location_id = d.dup_id;

WITH l_keep AS (
    SELECT LOWER(TRIM(name)) AS k, MIN(id::text)::uuid AS keep_id
    FROM locations WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name))
),
l_dupes AS (
    SELECT l.id AS dup_id, k.keep_id
    FROM locations l JOIN l_keep k ON LOWER(TRIM(l.name)) = k.k
    WHERE l.id <> k.keep_id
)
DELETE FROM locations l USING l_dupes d WHERE l.id = d.dup_id;

-- -----------------------
-- 4) PRODUCTOS por SKU
-- -----------------------
WITH p_keep AS (
    SELECT LOWER(TRIM(sku)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE sku IS NOT NULL AND TRIM(sku) <> ''
    GROUP BY LOWER(TRIM(sku))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p JOIN p_keep k ON LOWER(TRIM(p.sku)) = k.k
    WHERE p.id <> k.keep_id
)
DELETE FROM product_prices pp USING p_dupes d WHERE pp.product_id = d.dup_id
  AND EXISTS (SELECT 1 FROM product_prices pp2 WHERE pp2.product_id = d.keep_id AND pp2.price_list_id = pp.price_list_id);

WITH p_keep AS (
    SELECT LOWER(TRIM(sku)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE sku IS NOT NULL AND TRIM(sku) <> ''
    GROUP BY LOWER(TRIM(sku))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p JOIN p_keep k ON LOWER(TRIM(p.sku)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE product_prices pp SET product_id = d.keep_id FROM p_dupes d WHERE pp.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(sku)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE sku IS NOT NULL AND TRIM(sku) <> ''
    GROUP BY LOWER(TRIM(sku))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p JOIN p_keep k ON LOWER(TRIM(p.sku)) = k.k
    WHERE p.id <> k.keep_id
)
DELETE FROM product_location_stock pls USING p_dupes d WHERE pls.product_id = d.dup_id
  AND EXISTS (SELECT 1 FROM product_location_stock pls2 WHERE pls2.product_id = d.keep_id AND pls2.location_id = pls.location_id);

WITH p_keep AS (
    SELECT LOWER(TRIM(sku)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE sku IS NOT NULL AND TRIM(sku) <> ''
    GROUP BY LOWER(TRIM(sku))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p JOIN p_keep k ON LOWER(TRIM(p.sku)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE product_location_stock pls SET product_id = d.keep_id FROM p_dupes d WHERE pls.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(sku)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE sku IS NOT NULL AND TRIM(sku) <> ''
    GROUP BY LOWER(TRIM(sku))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p JOIN p_keep k ON LOWER(TRIM(p.sku)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE stock_movements sm SET product_id = d.keep_id FROM p_dupes d WHERE sm.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(sku)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE sku IS NOT NULL AND TRIM(sku) <> ''
    GROUP BY LOWER(TRIM(sku))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p JOIN p_keep k ON LOWER(TRIM(p.sku)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE product_variants pv SET product_id = d.keep_id FROM p_dupes d WHERE pv.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(sku)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE sku IS NOT NULL AND TRIM(sku) <> ''
    GROUP BY LOWER(TRIM(sku))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p JOIN p_keep k ON LOWER(TRIM(p.sku)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE sale_lines sl SET product_id = d.keep_id FROM p_dupes d WHERE sl.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(sku)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE sku IS NOT NULL AND TRIM(sku) <> ''
    GROUP BY LOWER(TRIM(sku))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p JOIN p_keep k ON LOWER(TRIM(p.sku)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE quotation_lines ql SET product_id = d.keep_id FROM p_dupes d WHERE ql.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(sku)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE sku IS NOT NULL AND TRIM(sku) <> ''
    GROUP BY LOWER(TRIM(sku))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p JOIN p_keep k ON LOWER(TRIM(p.sku)) = k.k
    WHERE p.id <> k.keep_id
)
DELETE FROM products p USING p_dupes d WHERE p.id = d.dup_id;

-- -----------------------
-- 5) PRODUCTOS por BARCODE
-- -----------------------
WITH p_keep AS (
    SELECT LOWER(TRIM(barcode)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
    GROUP BY LOWER(TRIM(barcode))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p JOIN p_keep k ON LOWER(TRIM(p.barcode)) = k.k
    WHERE p.id <> k.keep_id
)
DELETE FROM product_prices pp USING p_dupes d WHERE pp.product_id = d.dup_id
  AND EXISTS (SELECT 1 FROM product_prices pp2 WHERE pp2.product_id = d.keep_id AND pp2.price_list_id = pp.price_list_id);

WITH p_keep AS (
    SELECT LOWER(TRIM(barcode)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
    GROUP BY LOWER(TRIM(barcode))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p JOIN p_keep k ON LOWER(TRIM(p.barcode)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE product_prices pp SET product_id = d.keep_id FROM p_dupes d WHERE pp.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(barcode)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
    GROUP BY LOWER(TRIM(barcode))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p JOIN p_keep k ON LOWER(TRIM(p.barcode)) = k.k
    WHERE p.id <> k.keep_id
)
DELETE FROM product_location_stock pls USING p_dupes d WHERE pls.product_id = d.dup_id
  AND EXISTS (SELECT 1 FROM product_location_stock pls2 WHERE pls2.product_id = d.keep_id AND pls2.location_id = pls.location_id);

WITH p_keep AS (
    SELECT LOWER(TRIM(barcode)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
    GROUP BY LOWER(TRIM(barcode))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p JOIN p_keep k ON LOWER(TRIM(p.barcode)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE product_location_stock pls SET product_id = d.keep_id FROM p_dupes d WHERE pls.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(barcode)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
    GROUP BY LOWER(TRIM(barcode))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p JOIN p_keep k ON LOWER(TRIM(p.barcode)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE stock_movements sm SET product_id = d.keep_id FROM p_dupes d WHERE sm.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(barcode)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
    GROUP BY LOWER(TRIM(barcode))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p JOIN p_keep k ON LOWER(TRIM(p.barcode)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE product_variants pv SET product_id = d.keep_id FROM p_dupes d WHERE pv.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(barcode)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
    GROUP BY LOWER(TRIM(barcode))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p JOIN p_keep k ON LOWER(TRIM(p.barcode)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE sale_lines sl SET product_id = d.keep_id FROM p_dupes d WHERE sl.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(barcode)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
    GROUP BY LOWER(TRIM(barcode))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p JOIN p_keep k ON LOWER(TRIM(p.barcode)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE quotation_lines ql SET product_id = d.keep_id FROM p_dupes d WHERE ql.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(barcode)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
    GROUP BY LOWER(TRIM(barcode))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p JOIN p_keep k ON LOWER(TRIM(p.barcode)) = k.k
    WHERE p.id <> k.keep_id
)
DELETE FROM products p USING p_dupes d WHERE p.id = d.dup_id;

-- -----------------------
-- 6) PRODUCTOS por (nombre + marca)
-- -----------------------
WITH p_keep AS (
    SELECT LOWER(TRIM(name)) AS name_k, COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid) AS brand_k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name)), COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.name)) = k.name_k AND COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid) = k.brand_k
    WHERE p.id <> k.keep_id
)
DELETE FROM product_prices pp USING p_dupes d WHERE pp.product_id = d.dup_id
  AND EXISTS (SELECT 1 FROM product_prices pp2 WHERE pp2.product_id = d.keep_id AND pp2.price_list_id = pp.price_list_id);

WITH p_keep AS (
    SELECT LOWER(TRIM(name)) AS name_k, COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid) AS brand_k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name)), COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.name)) = k.name_k AND COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid) = k.brand_k
    WHERE p.id <> k.keep_id
)
UPDATE product_prices pp SET product_id = d.keep_id FROM p_dupes d WHERE pp.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(name)) AS name_k, COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid) AS brand_k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name)), COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.name)) = k.name_k AND COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid) = k.brand_k
    WHERE p.id <> k.keep_id
)
DELETE FROM product_location_stock pls USING p_dupes d WHERE pls.product_id = d.dup_id
  AND EXISTS (SELECT 1 FROM product_location_stock pls2 WHERE pls2.product_id = d.keep_id AND pls2.location_id = pls.location_id);

WITH p_keep AS (
    SELECT LOWER(TRIM(name)) AS name_k, COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid) AS brand_k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name)), COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.name)) = k.name_k AND COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid) = k.brand_k
    WHERE p.id <> k.keep_id
)
UPDATE product_location_stock pls SET product_id = d.keep_id FROM p_dupes d WHERE pls.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(name)) AS name_k, COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid) AS brand_k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name)), COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.name)) = k.name_k AND COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid) = k.brand_k
    WHERE p.id <> k.keep_id
)
UPDATE stock_movements sm SET product_id = d.keep_id FROM p_dupes d WHERE sm.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(name)) AS name_k, COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid) AS brand_k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name)), COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.name)) = k.name_k AND COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid) = k.brand_k
    WHERE p.id <> k.keep_id
)
UPDATE product_variants pv SET product_id = d.keep_id FROM p_dupes d WHERE pv.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(name)) AS name_k, COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid) AS brand_k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name)), COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.name)) = k.name_k AND COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid) = k.brand_k
    WHERE p.id <> k.keep_id
)
UPDATE sale_lines sl SET product_id = d.keep_id FROM p_dupes d WHERE sl.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(name)) AS name_k, COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid) AS brand_k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name)), COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.name)) = k.name_k AND COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid) = k.brand_k
    WHERE p.id <> k.keep_id
)
UPDATE quotation_lines ql SET product_id = d.keep_id FROM p_dupes d WHERE ql.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(name)) AS name_k, COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid) AS brand_k, MIN(id::text)::uuid AS keep_id
    FROM products WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name)), COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.name)) = k.name_k AND COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid) = k.brand_k
    WHERE p.id <> k.keep_id
)
DELETE FROM products p USING p_dupes d WHERE p.id = d.dup_id;

COMMIT;
