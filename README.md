# Enterprise Service Administration

Microservicio backend de administracion empresarial que actua como proxy hacia Keycloak y almacena perfiles extendidos en PostgreSQL. Sirve como **plantilla base** para nuevos proyectos que requieran autenticacion, manejo de usuarios, roles y permisos.

## Stack Tecnologico

| Componente | Version |
|---|---|
| Java | 21 |
| Spring Boot | 3.4.1 |
| Spring Security + OAuth2 Resource Server | JWT via Keycloak |
| Keycloak Admin Client | 26.0.7 |
| PostgreSQL | 15+ |
| Flyway | Migraciones automaticas |
| Lombok | Generacion de codigo |
| Springdoc OpenAPI | 2.7.0 (Swagger UI) |
| Docker | Multi-stage build |

## Arquitectura

```
Cliente (JWT Token)
    |
Spring Security (Validacion JWT vs Keycloak)
    |
Controller (REST endpoint)
    |
Service Layer
    |--- KeycloakUserService / KeycloakRoleService (Admin API)
    |--- Repositorios locales (PostgreSQL)
    |
Keycloak Admin REST API  /  PostgreSQL
    |
Response DTO --> JSON
```

El servicio sigue un patron **hibrido**: Keycloak es la fuente de verdad para usuarios y roles, mientras que la base de datos local enriquece el modelo con atributos especificos de la aplicacion (perfil, tags, mapeo de permisos).

## Estructura del Proyecto

```
com.hvati.administration/
├── config/
│   ├── SecurityConfig.java         # Spring Security + OAuth2 JWT
│   ├── KeycloakAdminConfig.java    # Beans del admin client de Keycloak
│   └── CorsConfig.java            # Configuracion CORS
├── controller/
│   ├── UserController.java         # /api/apps/contacts/*
│   ├── RoleController.java         # /api/admin/*
│   └── CurrentUserController.java  # /api/common/user
├── service/
│   ├── UserService.java            # Orquestacion de usuarios
│   ├── KeycloakUserService.java    # Operaciones directas con Keycloak
│   ├── RoleService.java            # Orquestacion de roles
│   ├── KeycloakRoleService.java    # Operaciones de roles en Keycloak
│   ├── PermissionService.java      # Gestion de permisos
│   └── TagService.java             # Gestion de etiquetas
├── repository/
│   ├── UserProfileRepository.java
│   ├── PermissionRepository.java
│   ├── RolePermissionRepository.java
│   └── TagRepository.java
├── entity/
│   ├── UserProfileEntity.java
│   ├── PermissionEntity.java
│   ├── RolePermissionEntity.java
│   └── TagEntity.java
├── dto/
│   ├── UserDto.java
│   ├── RoleDto.java
│   ├── CurrentUserDto.java
│   ├── UpdateUserRequest.java
│   ├── UpdateRoleRequest.java
│   ├── PermissionDto.java
│   └── TagDto.java
├── mapper/
│   ├── UserMapper.java
│   └── RoleMapper.java
└── exception/
    ├── GlobalExceptionHandler.java
    └── ResourceNotFoundException.java
```

## Requisitos Previos

- **Java 21** (JDK)
- **Maven 3.9+**
- **PostgreSQL 15+** ejecutandose y con la base de datos creada
- **Keycloak** ejecutandose y configurado con el realm correspondiente
- **Service Account** creado en Keycloak con permisos de admin del realm

## Configuracion

### Variables de Entorno

| Variable | Default | Descripcion |
|---|---|---|
| `DB_USERNAME` | `postgres` | Usuario de PostgreSQL |
| `DB_PASSWORD` | `postgres` | Password de PostgreSQL |
| `KC_CLIENT_ID` | `hvati-backend` | Client ID del service account en Keycloak |
| `KC_CLIENT_SECRET` | *(requerido)* | Client secret del service account |

### application.yml

Los valores principales a ajustar al crear un nuevo proyecto:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hvati_admin    # <- Nombre de la BD
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:postgres}

  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth-develop.hvati.cloud/realms/hvati  # <- URL del realm

keycloak:
  admin:
    server-url: https://auth-develop.hvati.cloud   # <- URL de Keycloak
    realm: hvati                                     # <- Nombre del realm
    client-id: ${KC_CLIENT_ID:hvati-backend}
    client-secret: ${KC_CLIENT_SECRET}

cors:
  allowed-origins: http://localhost:4200             # <- URL del frontend
```

## Ejecucion Local

### 1. Crear la base de datos

```sql
CREATE DATABASE hvati_admin;
```

### 2. Compilar

```bash
mvn clean package
```

### 3. Ejecutar

```bash
# Opcion A: Maven
mvn spring-boot:run

# Opcion B: JAR directamente
java -jar target/enterprise-service-administration-0.0.1-SNAPSHOT.jar

# Opcion C: Con variables de entorno
KC_CLIENT_SECRET=tu-secret java -jar target/enterprise-service-administration-0.0.1-SNAPSHOT.jar
```

El servicio estara disponible en `http://localhost:8080`.

### 4. Verificar

- **API:** http://localhost:8080/api
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs:** http://localhost:8080/api-docs

## Docker

### Construir imagen

```bash
docker build -t enterprise-service-administration .
```

### Ejecutar contenedor

```bash
docker run -p 8080:8080 \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=postgres \
  -e KC_CLIENT_SECRET=tu-secret \
  enterprise-service-administration
```

## Endpoints API

### Usuarios (`/api/apps/contacts`)

| Metodo | Ruta | Descripcion |
|---|---|---|
| `GET` | `/all` | Listar todos los usuarios |
| `GET` | `/search?query=` | Buscar usuarios |
| `POST` | `/contact` | Crear usuario (genera password temporal) |
| `PATCH` | `/contact` | Actualizar usuario |
| `DELETE` | `/contact?id=` | Eliminar usuario |
| `POST` | `/avatar` | Subir avatar |
| `GET` | `/countries` | Listado de paises (datos estaticos) |
| `GET` | `/tags` | Listar etiquetas |
| `POST` | `/tag` | Crear etiqueta |
| `PATCH` | `/tag` | Actualizar etiqueta |
| `DELETE` | `/tag?id=` | Eliminar etiqueta |

### Roles (`/api/admin`)

| Metodo | Ruta | Descripcion |
|---|---|---|
| `GET` | `/roles` | Listar realm roles de Keycloak |
| `GET` | `/roles/search?query=` | Buscar roles |
| `GET` | `/roles/{id}` | Obtener rol por ID |
| `POST` | `/roles` | Crear rol en Keycloak |
| `PATCH` | `/roles/{id}` | Actualizar rol |
| `DELETE` | `/roles/{id}` | Eliminar rol |
| `GET` | `/permissions` | Listar permisos disponibles |

### Usuario Actual (`/api/common/user`)

| Metodo | Ruta | Descripcion |
|---|---|---|
| `GET` | `/` | Datos del usuario autenticado |
| `PATCH` | `/` | Actualizar perfil propio |

## Seguridad

- **Endpoints publicos:** `/swagger-ui/**`, `/api-docs/**`, `/v3/api-docs/**`, `/actuator/**`
- **Todos los demas** requieren un JWT valido emitido por Keycloak
- **CSRF** deshabilitado (API stateless)
- **CORS** configurado para permitir el frontend en `localhost:4200`

## Base de Datos (Migraciones Flyway)

Las migraciones se ejecutan automaticamente al iniciar la aplicacion.

### V1__init.sql — Schema inicial

| Tabla | Descripcion |
|---|---|
| `user_profiles` | Perfiles extendidos (vinculados a Keycloak por `keycloak_id`) |
| `permissions` | Permisos de aplicacion |
| `role_permissions` | Mapeo entre roles de Keycloak y permisos |
| `tags` | Etiquetas para categorizar usuarios |
| `user_tags` | Relacion many-to-many usuarios-etiquetas |

### V2__seed_permissions.sql — Permisos iniciales

Permisos semilla: `users.read`, `users.write`, `roles.manage`, `reports.view`, `billing.view`.

## Logica de Negocio Clave

### Creacion de Usuarios
1. Se crea el usuario en Keycloak con datos placeholder
2. Se genera una **password temporal** (formato `Tmp-XXXXXXXXXXXX`)
3. Se crea el perfil local en PostgreSQL
4. La password temporal se devuelve en la respuesta (solo en creacion)
5. El usuario debe cambiarla en su primer inicio de sesion

### Asignacion de Roles
- Los roles son **realm roles de Keycloak**
- Al actualizar un usuario, se sincronizan los roles asignados
- Los roles internos de Keycloak (`default-roles-*`, `uma_*`, `offline_access`) se filtran automaticamente
- Si un rol no existe en Keycloak, se omite con un warning en el log

### Manejo de Errores
- `NotFoundException` de Keycloak se traduce a HTTP 404
- Errores de runtime incluyen timestamp, status, error y mensaje
- Todos los errores se loguean en el backend

## Adaptacion para Nuevo Proyecto

Al clonar esta plantilla para un nuevo proyecto:

1. **Renombrar** el `artifactId` y `groupId` en `pom.xml`
2. **Crear** la base de datos con el nombre deseado y actualizar `application.yml`
3. **Configurar** el realm y client de Keycloak correspondiente
4. **Ajustar** el `KC_CLIENT_SECRET` para el nuevo service account
5. **Actualizar** los origenes CORS (`cors.allowed-origins`) con la URL del frontend
6. **Agregar** nuevas migraciones Flyway (`V3__`, `V4__`, etc.) segun necesidad
7. **Extender** los permisos semilla en una nueva migracion
8. **Agregar** nuevos controllers/services para la logica de negocio especifica
