# Configuración de Keycloak (realm hvati)

Este documento describe cómo configurar Keycloak desde cero para el ecosistema **hvati**: frontend Angular (`hvati-web`) y backend Spring Boot (`hvati-backend`).

- **URL de Keycloak:** `https://auth-develop.hvati.cloud`
- **Realm:** `hvati`

---

## 1. Acceso al Admin Console

1. Abre `https://auth-develop.hvati.cloud`
2. Inicia sesión con el usuario administrador del realm (creado al levantar Keycloak, p. ej. el que definiste en `KEYCLOAK_ADMIN` / `KEYCLOAK_ADMIN_PASSWORD` en el compose).
3. Selecciona el realm **master** si es necesario, luego crea o selecciona el realm **hvati** (ver siguiente sección).

---

## 2. Realm `hvati`

### Crear el realm (si está en blanco)

1. En el menú izquierdo: **Realm selector** (arriba) → **Create realm**.
2. **Realm name:** `hvati`.
3. Clic en **Create**.

### Ajustes recomendados del realm

- **Realm settings → General:** dejar valores por defecto.
- **Realm settings → Login:** activar **User registration** si quieres que los usuarios se registren desde el front.
- **Realm settings → Tokens:** ajustar duración de tokens si lo necesitas (p. ej. Access Token Lifespan).

---

## 3. Cliente para el frontend: `hvati-web`

El frontend Angular usa este cliente con **Authorization Code + PKCE**.

1. **Clients** → **Create client**.
2. **General settings**
   - **Client type:** `OpenID Connect`
   - **Client ID:** `hvati-web`
3. **Capability config**
   - **Client authentication:** **OFF** (cliente público).
   - **Authorization:** **ON**
   - **Authentication flow:** marcar **Standard flow** y **Direct access grants** (opcional, para refresh).
   - **Post login redirect URIs:** (ver abajo)
   - **Post logout redirect URIs:** (ver abajo)
   - **Web origins:** (ver abajo)
4. Clic en **Next** y luego en **Save**.

### Valid redirect URIs (ejemplos)

- `http://localhost:4200/*`
- `https://tu-dominio-frontend.com/*`

En producción añade la URL real de tu app (ej. `https://app.hvati.cloud/*`).

### Valid post logout redirect URIs

- `http://localhost:4200`
- `https://tu-dominio-frontend.com`

### Web origins

- `http://localhost:4200`
- `https://tu-dominio-frontend.com`

(O `+` para usar las mismas que los redirect URIs.)

---

## 4. Cliente para el backend: `hvati-backend`

El backend Spring Boot usa este cliente con **Client credentials** para llamar a la Admin API de Keycloak.

1. **Clients** → **Create client**.
2. **General settings**
   - **Client type:** `OpenID Connect`
   - **Client ID:** `hvati-backend`
3. **Capability config**
   - **Client authentication:** **ON**
   - **Authorization:** **OFF**
   - **Authentication flow:** marcar **Service accounts roles** (client credentials).
4. Clic en **Next** → **Save**.
5. En la pestaña **Credentials** del cliente `hvati-backend`:
   - Copia el **Client secret** y configúralo en el backend (variable de entorno `KC_CLIENT_SECRET` o `application.yml`).

### Permisos del service account (Admin API)

Para que el backend pueda gestionar usuarios y roles:

1. En el cliente **hvati-backend** → pestaña **Service account roles** (o **Permissions**).
2. Asigna roles de **realm-management** al service account, por ejemplo:
   - **manage-users**
   - **view-users**
   - **manage-realm**
   - **view-realm**
   - O el rol **realm-management** completo si lo prefieres.

(En Keycloak 26: **Clients** → **hvati-backend** → **Service account roles** → **Assign role** → **Filter by realm roles** → **realm-management** → seleccionar los anteriores.)

---

## 5. Roles del realm (para la aplicación)

La app usa **realm roles** para autorización. Crea al menos estos roles en el realm **hvati**:

1. **Realm roles** → **Create role**.
2. Crear:
   - **ADMIN** — acceso completo (usuarios, roles, ventas, clientes, productos, caja, reportes).
   - **VENTAS** — ventas, pedidos, clientes, productos.

Los nombres deben coincidir con los que usa el frontend y el backend (p. ej. `ROLE_PERMISSIONS_MAP` en el front y mapeo en la base de datos del backend).

### Asignar roles a usuarios

- **Users** → seleccionar usuario → **Role mapping** → **Assign role** → elegir **ADMIN** o **VENTAS**.

---

## 6. Usuario de prueba

1. **Users** → **Add user**.
2. **Username:** (ej. `admin`)
3. **Email:** (ej. `admin@hvati.cloud`)
4. **First name / Last name** (opcional).
5. **Email verified:** ON si quieres.
6. **Create**.
7. **Credentials** → **Set password** (contraseña temporal o permanente).
8. **Role mapping** → **Assign role** → **ADMIN** (o **VENTAS**).

---

## 7. Resumen para el backend (variables de entorno)

| Variable              | Descripción                          | Ejemplo                                      |
|-----------------------|--------------------------------------|----------------------------------------------|
| `KC_CLIENT_ID`        | Client ID del service account        | `hvati-backend`                              |
| `KC_CLIENT_SECRET`    | Secret del cliente hvati-backend     | (el que muestra Keycloak en Credentials)     |

En `application.yml` (o perfil) se usan además:

- **Keycloak URL:** `https://auth-develop.hvati.cloud`
- **Realm:** `hvati`
- **JWT issuer-uri:** `https://auth-develop.hvati.cloud/realms/hvati`

---

## 8. Resumen para el frontend (Angular)

En `keycloak.config.ts`:

- **url:** `https://auth-develop.hvati.cloud`
- **realm:** `hvati`
- **clientId:** `hvati-web`

El frontend usa **PKCE** (S256); no hace falta client secret.

---

## 9. Comprobar que todo funciona

1. **Frontend:** abrir la app (ej. `http://localhost:4200`), hacer login; deberías ser redirigido a Keycloak y luego de vuelta a la app con sesión.
2. **Backend:** llamar a un endpoint protegido con el JWT que devuelve el frontend (Bearer token). El backend debe validar el token contra `https://auth-develop.hvati.cloud/realms/hvati` y aceptar el usuario con sus roles.
3. **Admin API:** el backend debe poder listar usuarios/roles usando el client `hvati-backend` y su secret; si falla, revisar permisos del service account (realm-management).

---

## 10. Referencia rápida (URLs y nombres)

| Concepto        | Valor                               |
|-----------------|-------------------------------------|
| Keycloak URL    | https://auth-develop.hvati.cloud    |
| Realm           | hvati                               |
| Cliente frontend| hvati-web (público, PKCE)           |
| Cliente backend | hvati-backend (confidencial)        |
| Realm roles     | ADMIN, VENTAS                       |

---

## 11. Solución: Error 500 con mensaje "HTTP 403 Forbidden" al entrar a pestañas (Contacts/Users)

Si el login en el front funciona pero al abrir **Contacts** (o Users) ves **500 Internal Server Error** y en el body del error aparece **"message": "HTTP 403 Forbidden"**, el backend está recibiendo **403** de la **Admin API de Keycloak**. Es decir: el **service account** del cliente `hvati-backend` **no tiene permisos** para listar/gestar usuarios en el realm.

### Pasos en Keycloak (realm hvati)

1. **Clients** → abre el cliente **hvati-backend**.
2. Entra en la pestaña **Service account roles** (o **Permissions** / **Roles** según versión).
3. Pulsa **Assign role**.
4. En el filtro, elige **Filter by client roles** (o "Filter by client") y selecciona el cliente **realm-management**.
5. Marca al menos estos roles y asigna:
   - **view-users**
   - **manage-users**
   - **view-realm**
   - **manage-realm**
6. Guarda / **Assign**.

Con eso el backend podrá listar usuarios, roles y usar la Admin API. Reinicia el backend si hace falta y vuelve a cargar la pestaña Contacts.
