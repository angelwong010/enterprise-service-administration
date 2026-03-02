-- Evita duplicados cuando no hay SKU/Barcode: unicidad por (nombre + marca) normalizado.
-- Nota: la marca puede ser NULL. Para índices únicos con NULL, usamos COALESCE a UUID fijo.

-- 1) Deduplicar existentes por (LOWER(TRIM(name)), COALESCE(brand_id, 0000...))
WITH p_keep AS (
    SELECT
        LOWER(TRIM(name)) AS name_k,
        COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid) AS brand_k,
        MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name)), COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k
        ON LOWER(TRIM(p.name)) = k.name_k
       AND COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid) = k.brand_k
    WHERE p.id <> k.keep_id
)
-- Evitar choque UNIQUE(product_id, price_list_id) al mover precios al keep_id
DELETE FROM product_prices pp
USING p_dupes d
WHERE pp.product_id = d.dup_id
  AND EXISTS (
      SELECT 1 FROM product_prices pp2
      WHERE pp2.product_id = d.keep_id
        AND pp2.price_list_id = pp.price_list_id
  );

WITH p_keep AS (
    SELECT
        LOWER(TRIM(name)) AS name_k,
        COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid) AS brand_k,
        MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name)), COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k
        ON LOWER(TRIM(p.name)) = k.name_k
       AND COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid) = k.brand_k
    WHERE p.id <> k.keep_id
)
UPDATE product_prices pp
SET product_id = d.keep_id
FROM p_dupes d
WHERE pp.product_id = d.dup_id;

WITH p_keep AS (
    SELECT
        LOWER(TRIM(name)) AS name_k,
        COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid) AS brand_k,
        MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name)), COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k
        ON LOWER(TRIM(p.name)) = k.name_k
       AND COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid) = k.brand_k
    WHERE p.id <> k.keep_id
)
-- Evitar choque UNIQUE(product_id, location_id) al mover stock al keep_id
DELETE FROM product_location_stock pls
USING p_dupes d
WHERE pls.product_id = d.dup_id
  AND EXISTS (
      SELECT 1 FROM product_location_stock pls2
      WHERE pls2.product_id = d.keep_id
        AND pls2.location_id = pls.location_id
  );

WITH p_keep AS (
    SELECT
        LOWER(TRIM(name)) AS name_k,
        COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid) AS brand_k,
        MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name)), COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k
        ON LOWER(TRIM(p.name)) = k.name_k
       AND COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid) = k.brand_k
    WHERE p.id <> k.keep_id
)
UPDATE product_location_stock pls
SET product_id = d.keep_id
FROM p_dupes d
WHERE pls.product_id = d.dup_id;

WITH p_keep AS (
    SELECT
        LOWER(TRIM(name)) AS name_k,
        COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid) AS brand_k,
        MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name)), COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k
        ON LOWER(TRIM(p.name)) = k.name_k
       AND COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid) = k.brand_k
    WHERE p.id <> k.keep_id
)
UPDATE stock_movements sm
SET product_id = d.keep_id
FROM p_dupes d
WHERE sm.product_id = d.dup_id;

WITH p_keep AS (
    SELECT
        LOWER(TRIM(name)) AS name_k,
        COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid) AS brand_k,
        MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name)), COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k
        ON LOWER(TRIM(p.name)) = k.name_k
       AND COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid) = k.brand_k
    WHERE p.id <> k.keep_id
)
UPDATE product_variants pv
SET product_id = d.keep_id
FROM p_dupes d
WHERE pv.product_id = d.dup_id;

WITH p_keep AS (
    SELECT
        LOWER(TRIM(name)) AS name_k,
        COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid) AS brand_k,
        MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name)), COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k
        ON LOWER(TRIM(p.name)) = k.name_k
       AND COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid) = k.brand_k
    WHERE p.id <> k.keep_id
)
UPDATE sale_lines sl
SET product_id = d.keep_id
FROM p_dupes d
WHERE sl.product_id = d.dup_id;

WITH p_keep AS (
    SELECT
        LOWER(TRIM(name)) AS name_k,
        COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid) AS brand_k,
        MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name)), COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k
        ON LOWER(TRIM(p.name)) = k.name_k
       AND COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid) = k.brand_k
    WHERE p.id <> k.keep_id
)
UPDATE quotation_lines ql
SET product_id = d.keep_id
FROM p_dupes d
WHERE ql.product_id = d.dup_id;

WITH p_keep AS (
    SELECT
        LOWER(TRIM(name)) AS name_k,
        COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid) AS brand_k,
        MIN(id::text)::uuid AS keep_id
    FROM products
    WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name)), COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
),
p_dupes AS (
    SELECT p.id AS dup_id, k.keep_id
    FROM products p
    JOIN p_keep k
        ON LOWER(TRIM(p.name)) = k.name_k
       AND COALESCE(p.brand_id, '00000000-0000-0000-0000-000000000000'::uuid) = k.brand_k
    WHERE p.id <> k.keep_id
)
DELETE FROM products p
USING p_dupes d
WHERE p.id = d.dup_id;

CREATE UNIQUE INDEX IF NOT EXISTS uq_products_name_brand_ci
ON products (
    LOWER(TRIM(name)),
    COALESCE(brand_id, '00000000-0000-0000-0000-000000000000'::uuid)
);

