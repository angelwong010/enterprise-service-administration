-- Agrega permiso para Dashboard y lo asigna a roles principales.
-- Se crea como una nueva migración para no modificar checksums previos.

INSERT INTO permissions (id, label, category) VALUES
('DASHBOARD_READ', 'Ver dashboard', 'Dashboard')
ON CONFLICT (id) DO NOTHING;

-- ADMIN y VENTAS pueden ver el dashboard
INSERT INTO role_permissions (id, role_name, permission_id)
SELECT gen_random_uuid(), 'ADMIN', p.id FROM permissions p
WHERE p.id IN ('DASHBOARD_READ')
ON CONFLICT (role_name, permission_id) DO NOTHING;

INSERT INTO role_permissions (id, role_name, permission_id)
SELECT gen_random_uuid(), 'VENTAS', p.id FROM permissions p
WHERE p.id IN ('DASHBOARD_READ')
ON CONFLICT (role_name, permission_id) DO NOTHING;

