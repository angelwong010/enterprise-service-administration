-- Evitar duplicados en "listas" (price lists, marcas y ubicaciones).
-- 1) Deduplica por LOWER(TRIM(name)) preservando el registro con id "menor"
-- 2) Reasigna llaves foráneas
-- 3) Agrega índices únicos case-insensitive para que nunca se vuelvan a duplicar

-- -----------------------
-- PRICE LISTS
-- -----------------------
WITH pl_keep AS (
    -- PostgreSQL no soporta MIN(uuid); usamos MIN(id::text)::uuid para elegir un "keep" determinístico
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
UPDATE clients c
SET price_list_id = d.keep_id
FROM pl_dupes d
WHERE c.price_list_id = d.dup_id;

WITH pl_keep AS (
    -- PostgreSQL no soporta MIN(uuid); usamos MIN(id::text)::uuid para elegir un "keep" determinístico
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
-- Si un producto ya tiene precio en la lista "keep", elimina el duplicado para evitar choque UNIQUE(product_id, price_list_id)
DELETE FROM product_prices pp
USING pl_dupes d
WHERE pp.price_list_id = d.dup_id
  AND EXISTS (
      SELECT 1
      FROM product_prices pp2
      WHERE pp2.product_id = pp.product_id
        AND pp2.price_list_id = d.keep_id
  );

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
UPDATE product_prices pp
SET price_list_id = d.keep_id
FROM pl_dupes d
WHERE pp.price_list_id = d.dup_id;

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
DELETE FROM price_lists pl
USING pl_dupes d
WHERE pl.id = d.dup_id;

CREATE UNIQUE INDEX IF NOT EXISTS uq_price_lists_name_ci
ON price_lists (LOWER(TRIM(name)));

-- -----------------------
-- BRANDS
-- -----------------------
WITH b_keep AS (
    SELECT LOWER(TRIM(name)) AS k, MIN(id::text)::uuid AS keep_id
    FROM brands
    WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name))
),
b_dupes AS (
    SELECT b.id AS dup_id, k.keep_id
    FROM brands b
    JOIN b_keep k ON LOWER(TRIM(b.name)) = k.k
    WHERE b.id <> k.keep_id
)
UPDATE products p
SET brand_id = d.keep_id
FROM b_dupes d
WHERE p.brand_id = d.dup_id;

WITH b_keep AS (
    SELECT LOWER(TRIM(name)) AS k, MIN(id::text)::uuid AS keep_id
    FROM brands
    WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name))
),
b_dupes AS (
    SELECT b.id AS dup_id, k.keep_id
    FROM brands b
    JOIN b_keep k ON LOWER(TRIM(b.name)) = k.k
    WHERE b.id <> k.keep_id
)
DELETE FROM brands b
USING b_dupes d
WHERE b.id = d.dup_id;

CREATE UNIQUE INDEX IF NOT EXISTS uq_brands_name_ci
ON brands (LOWER(TRIM(name)));

-- -----------------------
-- LOCATIONS
-- -----------------------
WITH l_keep AS (
    SELECT LOWER(TRIM(name)) AS k, MIN(id::text)::uuid AS keep_id
    FROM locations
    WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name))
),
l_dupes AS (
    SELECT l.id AS dup_id, k.keep_id
    FROM locations l
    JOIN l_keep k ON LOWER(TRIM(l.name)) = k.k
    WHERE l.id <> k.keep_id
)
-- Evitar choque UNIQUE(product_id, location_id) al reasignar a la ubicación "keep"
DELETE FROM product_location_stock pls
USING l_dupes d
WHERE pls.location_id = d.dup_id
  AND EXISTS (
      SELECT 1
      FROM product_location_stock pls2
      WHERE pls2.product_id = pls.product_id
        AND pls2.location_id = d.keep_id
  );

WITH l_keep AS (
    SELECT LOWER(TRIM(name)) AS k, MIN(id::text)::uuid AS keep_id
    FROM locations
    WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name))
),
l_dupes AS (
    SELECT l.id AS dup_id, k.keep_id
    FROM locations l
    JOIN l_keep k ON LOWER(TRIM(l.name)) = k.k
    WHERE l.id <> k.keep_id
)
UPDATE product_location_stock pls
SET location_id = d.keep_id
FROM l_dupes d
WHERE pls.location_id = d.dup_id;

WITH l_keep AS (
    SELECT LOWER(TRIM(name)) AS k, MIN(id::text)::uuid AS keep_id
    FROM locations
    WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name))
),
l_dupes AS (
    SELECT l.id AS dup_id, k.keep_id
    FROM locations l
    JOIN l_keep k ON LOWER(TRIM(l.name)) = k.k
    WHERE l.id <> k.keep_id
)
UPDATE stock_movements sm
SET location_id = d.keep_id
FROM l_dupes d
WHERE sm.location_id = d.dup_id;

WITH l_keep AS (
    SELECT LOWER(TRIM(name)) AS k, MIN(id::text)::uuid AS keep_id
    FROM locations
    WHERE name IS NOT NULL AND TRIM(name) <> ''
    GROUP BY LOWER(TRIM(name))
),
l_dupes AS (
    SELECT l.id AS dup_id, k.keep_id
    FROM locations l
    JOIN l_keep k ON LOWER(TRIM(l.name)) = k.k
    WHERE l.id <> k.keep_id
)
DELETE FROM locations l
USING l_dupes d
WHERE l.id = d.dup_id;

CREATE UNIQUE INDEX IF NOT EXISTS uq_locations_name_ci
ON locations (LOWER(TRIM(name)));

