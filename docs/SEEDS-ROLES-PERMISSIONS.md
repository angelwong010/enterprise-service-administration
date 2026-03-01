# Seeds: roles y permisos

Para levantar el software en un servidor nuevo, los roles y permisos quedan alineados entre base de datos y Keycloak.

## Base de datos (Flyway)

- **V2__seed_permissions.sql**: permisos iniciales (legacy: `users.read`, `users.write`, etc.).
- **V9__seed_permissions_and_roles.sql**:
  - Inserta permisos alineados con el frontend (`USERS_READ`, `USERS_WRITE`, `VENTAS_READ`, `VENTAS_CREATE`, `CLIENTES_READ`, `CLIENTES_CREATE`, `PRODUCTOS_READ`, `PRODUCTOS_CREATE`, `CAJA_READ`, `CAJA_CREATE`, `REPORTES_READ`, `REPORTES_CREATE`, `ROLES_READ`, `ROLES_WRITE`, `ORDERS_READ`, `ORDERS_CREATE`).
  - Asigna permisos a los roles **ADMIN** y **VENTAS** en la tabla `role_permissions` (por nombre de rol, que debe coincidir con Keycloak).

Usa `ON CONFLICT DO NOTHING` para que en BD ya existentes no falle ni duplique.

## Keycloak (arranque de la aplicación)

- **KeycloakSeedRunner** (ApplicationRunner): al iniciar la app, comprueba si existen en el realm los roles **ADMIN** y **VENTAS**. Si no existen, los crea con descripciones por defecto.
- Así, en un servidor nuevo (realm vacío o sin esos roles), el primer arranque deja los roles listos para asignar a usuarios y para que `role_permissions` tenga efecto (el frontend usa el nombre del rol del token para saber permisos).

## Congruencia con el frontend

- **Permisos**: los `id` en la tabla `permissions` coinciden con `PERMISSIONS` en `app/core/auth/permissions.ts` (por ejemplo `USERS_READ`, `VENTAS_CREATE`).
- **Roles**: los nombres **ADMIN** y **VENTAS** coinciden con las claves de `ROLE_PERMISSIONS_MAP` en `app/core/auth/role-permissions.map.ts`.
- El módulo Admin (roles) carga permisos desde `GET /api/admin/permissions` y asigna permisos por `id`; esos `id` deben ser los mismos que en la BD.

## Resumen para nuevo servidor

1. Levantar PostgreSQL y aplicar migraciones (Flyway): se crean/actualizan tablas y se ejecutan V2 y V9.
2. Configurar Keycloak (realm, client con client-credentials para el backend).
3. Arrancar el backend: KeycloakSeedRunner crea ADMIN y VENTAS en el realm si no existen.
4. Asignar a los usuarios los roles ADMIN o VENTAS en Keycloak; la app usará `role_permissions` para saber qué permisos tiene cada rol.
