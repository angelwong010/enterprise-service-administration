-- Permisos alineados con el frontend (PERMISSIONS en app/core/auth/permissions.ts)
-- Para nuevos servidores: crea todos los permisos y asigna a roles ADMIN y VENTAS.
-- ON CONFLICT DO NOTHING permite ejecutar en BD ya existente sin duplicados.

INSERT INTO permissions (id, label, category) VALUES
('USERS_READ', 'Ver usuarios', 'Usuarios'),
('USERS_WRITE', 'Crear/editar usuarios', 'Usuarios'),
('ROLES_READ', 'Ver roles', 'Seguridad'),
('ROLES_WRITE', 'Administrar roles', 'Seguridad'),
('ORDERS_READ', 'Ver órdenes', 'Ventas'),
('ORDERS_CREATE', 'Crear órdenes', 'Ventas'),
('VENTAS_READ', 'Ver ventas', 'Ventas'),
('VENTAS_CREATE', 'Crear ventas', 'Ventas'),
('CLIENTES_READ', 'Ver clientes', 'Clientes'),
('CLIENTES_CREATE', 'Crear/editar clientes', 'Clientes'),
('PRODUCTOS_READ', 'Ver productos', 'Productos'),
('PRODUCTOS_CREATE', 'Crear/editar productos', 'Productos'),
('CAJA_READ', 'Ver caja', 'Caja'),
('CAJA_CREATE', 'Operar caja', 'Caja'),
('REPORTES_READ', 'Ver reportes', 'Reportes'),
('REPORTES_CREATE', 'Crear reportes', 'Reportes')
ON CONFLICT (id) DO NOTHING;

-- Asignación de permisos a roles (role_name = nombre del rol en Keycloak)
-- ADMIN: todos los permisos de lectura y creación relevantes
INSERT INTO role_permissions (id, role_name, permission_id)
SELECT gen_random_uuid(), 'ADMIN', p.id FROM permissions p
WHERE p.id IN (
  'USERS_READ','USERS_WRITE','ROLES_READ','ROLES_WRITE',
  'ORDERS_READ','ORDERS_CREATE','VENTAS_READ','VENTAS_CREATE',
  'CLIENTES_READ','CLIENTES_CREATE','PRODUCTOS_READ','PRODUCTOS_CREATE',
  'CAJA_READ','CAJA_CREATE','REPORTES_READ','REPORTES_CREATE'
)
ON CONFLICT (role_name, permission_id) DO NOTHING;

-- VENTAS: permisos de ventas, clientes, productos y caja
INSERT INTO role_permissions (id, role_name, permission_id)
SELECT gen_random_uuid(), 'VENTAS', p.id FROM permissions p
WHERE p.id IN (
  'ORDERS_READ','ORDERS_CREATE','VENTAS_READ','VENTAS_CREATE',
  'CLIENTES_READ','CLIENTES_CREATE','PRODUCTOS_READ',
  'CAJA_READ','CAJA_CREATE'
)
ON CONFLICT (role_name, permission_id) DO NOTHING;
