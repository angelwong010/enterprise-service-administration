-- Deduplica productos creados por cargas repetidas y evita duplicados a futuro.
-- Regla: si el SKU/Barcode coincide (case-insensitive), se conserva un "keep" determinístico.

-- ----------------------------------------
-- 1) Dedup por SKU (LOWER(TRIM(sku)))
-- ----------------------------------------
WITH p_keep AS (
    SELECT LOWER(TRIM(sku)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE sku IS NOT NULL AND TRIM(sku) <> ''
    GROUP BY LOWER(TRIM(sku))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.sku)) = k.k
    WHERE p.id <> k.keep_id
)
-- Evitar choque UNIQUE(product_id, price_list_id)
DELETE FROM product_prices pp
USING p_dupes d
WHERE pp.product_id = d.dup_id
  AND EXISTS (
      SELECT 1 FROM product_prices pp2
      WHERE pp2.product_id = d.keep_id
        AND pp2.price_list_id = pp.price_list_id
  );

WITH p_keep AS (
    SELECT LOWER(TRIM(sku)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE sku IS NOT NULL AND TRIM(sku) <> ''
    GROUP BY LOWER(TRIM(sku))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.sku)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE product_prices pp
SET product_id = d.keep_id
FROM p_dupes d
WHERE pp.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(sku)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE sku IS NOT NULL AND TRIM(sku) <> ''
    GROUP BY LOWER(TRIM(sku))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.sku)) = k.k
    WHERE p.id <> k.keep_id
)
-- Evitar choque UNIQUE(product_id, location_id)
DELETE FROM product_location_stock pls
USING p_dupes d
WHERE pls.product_id = d.dup_id
  AND EXISTS (
      SELECT 1 FROM product_location_stock pls2
      WHERE pls2.product_id = d.keep_id
        AND pls2.location_id = pls.location_id
  );

WITH p_keep AS (
    SELECT LOWER(TRIM(sku)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE sku IS NOT NULL AND TRIM(sku) <> ''
    GROUP BY LOWER(TRIM(sku))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.sku)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE product_location_stock pls
SET product_id = d.keep_id
FROM p_dupes d
WHERE pls.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(sku)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE sku IS NOT NULL AND TRIM(sku) <> ''
    GROUP BY LOWER(TRIM(sku))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.sku)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE stock_movements sm
SET product_id = d.keep_id
FROM p_dupes d
WHERE sm.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(sku)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE sku IS NOT NULL AND TRIM(sku) <> ''
    GROUP BY LOWER(TRIM(sku))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.sku)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE product_variants pv
SET product_id = d.keep_id
FROM p_dupes d
WHERE pv.product_id = d.dup_id;

-- Estas tablas no tienen FK, pero mantenemos consistencia si ya tienen product_id
WITH p_keep AS (
    SELECT LOWER(TRIM(sku)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE sku IS NOT NULL AND TRIM(sku) <> ''
    GROUP BY LOWER(TRIM(sku))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.sku)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE sale_lines sl
SET product_id = d.keep_id
FROM p_dupes d
WHERE sl.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(sku)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE sku IS NOT NULL AND TRIM(sku) <> ''
    GROUP BY LOWER(TRIM(sku))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.sku)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE quotation_lines ql
SET product_id = d.keep_id
FROM p_dupes d
WHERE ql.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(sku)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE sku IS NOT NULL AND TRIM(sku) <> ''
    GROUP BY LOWER(TRIM(sku))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.sku)) = k.k
    WHERE p.id <> k.keep_id
)
DELETE FROM products p
USING p_dupes d
WHERE p.id = d.dup_id;

-- ----------------------------------------
-- 2) Dedup por BARCODE (LOWER(TRIM(barcode)))
-- ----------------------------------------
WITH p_keep AS (
    SELECT LOWER(TRIM(barcode)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
    GROUP BY LOWER(TRIM(barcode))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.barcode)) = k.k
    WHERE p.id <> k.keep_id
)
DELETE FROM product_prices pp
USING p_dupes d
WHERE pp.product_id = d.dup_id
  AND EXISTS (
      SELECT 1 FROM product_prices pp2
      WHERE pp2.product_id = d.keep_id
        AND pp2.price_list_id = pp.price_list_id
  );

WITH p_keep AS (
    SELECT LOWER(TRIM(barcode)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
    GROUP BY LOWER(TRIM(barcode))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.barcode)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE product_prices pp
SET product_id = d.keep_id
FROM p_dupes d
WHERE pp.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(barcode)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
    GROUP BY LOWER(TRIM(barcode))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.barcode)) = k.k
    WHERE p.id <> k.keep_id
)
DELETE FROM product_location_stock pls
USING p_dupes d
WHERE pls.product_id = d.dup_id
  AND EXISTS (
      SELECT 1 FROM product_location_stock pls2
      WHERE pls2.product_id = d.keep_id
        AND pls2.location_id = pls.location_id
  );

WITH p_keep AS (
    SELECT LOWER(TRIM(barcode)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
    GROUP BY LOWER(TRIM(barcode))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.barcode)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE product_location_stock pls
SET product_id = d.keep_id
FROM p_dupes d
WHERE pls.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(barcode)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
    GROUP BY LOWER(TRIM(barcode))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.barcode)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE stock_movements sm
SET product_id = d.keep_id
FROM p_dupes d
WHERE sm.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(barcode)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
    GROUP BY LOWER(TRIM(barcode))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.barcode)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE product_variants pv
SET product_id = d.keep_id
FROM p_dupes d
WHERE pv.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(barcode)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
    GROUP BY LOWER(TRIM(barcode))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.barcode)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE sale_lines sl
SET product_id = d.keep_id
FROM p_dupes d
WHERE sl.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(barcode)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
    GROUP BY LOWER(TRIM(barcode))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.barcode)) = k.k
    WHERE p.id <> k.keep_id
)
UPDATE quotation_lines ql
SET product_id = d.keep_id
FROM p_dupes d
WHERE ql.product_id = d.dup_id;

WITH p_keep AS (
    SELECT LOWER(TRIM(barcode)) AS k, MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE barcode IS NOT NULL AND TRIM(barcode) <> ''
    GROUP BY LOWER(TRIM(barcode))
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k ON LOWER(TRIM(p.barcode)) = k.k
    WHERE p.id <> k.keep_id
)
DELETE FROM products p
USING p_dupes d
WHERE p.id = d.dup_id;

-- ----------------------------------------
-- 3) Índices únicos para evitar duplicados futuros
-- ----------------------------------------
CREATE UNIQUE INDEX IF NOT EXISTS uq_products_sku_ci
ON products (LOWER(TRIM(sku)))
WHERE sku IS NOT NULL AND TRIM(sku) <> '';

CREATE UNIQUE INDEX IF NOT EXISTS uq_products_barcode_ci
ON products (LOWER(TRIM(barcode)))
WHERE barcode IS NOT NULL AND TRIM(barcode) <> '';

