-- Asegurar que exista una ubicación por defecto "Sucursal principal"
-- (Si V6 ya insertó "Sucursal Principal", normalizamos el nombre; si no hay ubicaciones, insertamos una.)
INSERT INTO locations (id, name)
SELECT gen_random_uuid(), 'Sucursal principal'
WHERE NOT EXISTS (SELECT 1 FROM locations LIMIT 1);

UPDATE locations
SET name = 'Sucursal principal'
WHERE LOWER(TRIM(name)) = 'sucursal principal';
