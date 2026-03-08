# Cómo purgar la base de datos

Tu proyecto usa **PostgreSQL** y **Flyway**. Tienes dos formas de “purgar” la base:

---

## Eliminar solo datos duplicados (sin vaciar todo)

Si quieres **quitar duplicados** y conservar un solo registro por grupo (mismo nombre, SKU, email, etc.):

1. **Detectar duplicados** (solo lectura):
   ```bash
   psql "$DB_URL" -f src/main/resources/db/find-duplicates.sql
   ```
   Verás por tabla qué claves están duplicadas y cuántos registros hay.

2. **Eliminar duplicados** (haz backup antes):
   ```bash
   psql "$DB_URL" -f src/main/resources/db/dedupe-data.sql
   ```
   El script usa la misma lógica que las migraciones V11, V12, V13: mantiene un registro por grupo (el de `id` mínimo), reasigna las FKs y borra el resto. Orden: listas de precios → marcas → ubicaciones → productos (por SKU, por código de barras, por nombre+marca). **Clientes** no se deduplican aquí; revisa el reporte de `find-duplicates.sql` y decide manualmente si fusionar por email.

---

## Resumen de tablas (esquema actual)

| Origen   | Tablas |
|----------|--------|
| V1 init  | `user_profiles`, `permissions`, `role_permissions`, `tags`, `user_tags` |
| V3       | `price_lists`, `clients`, `client_addresses`, `client_billing_data`, `sales` |
| V5       | `categories`, `brands`, `locations`, `products`, `product_prices`, `product_location_stock`, `product_variants`, `product_variant_options`, `stock_movements` |
| V8       | `sale_lines`, `sale_history`, `quotations`, `quotation_lines` |

Las relaciones (FK) obligan a borrar en este orden: primero tablas hijas (líneas, historial, direcciones, etc.) y al final las raíz (clientes, productos, listas de precios, etc.).

---

## Opción 1: Solo vaciar datos (mantener esquema)

Borra todos los datos pero **no** elimina tablas ni migraciones. Útil para desarrollo o para dejar la BD “en blanco” y volver a cargar datos.

### Pasos

1. Conectarte a tu base (misma que usa `DB_URL` en tu entorno).
2. Ejecutar el script de purga:

```bash
# Con variables de entorno (ajusta DB_URL o usa -h -U -d)
psql "$DB_URL" -f src/main/resources/db/purge-data.sql
```

O desde **DBeaver / pgAdmin**: abre `src/main/resources/db/purge-data.sql` y ejecútalo contra la base del proyecto.

**Importante:** Después de purgar, los seeds (V2, V4, V6, V9, etc.) **no** se vuelven a ejecutar automáticamente: Flyway ya los marcó como aplicados. Si quieres datos iniciales de nuevo, puedes:

- Usar la **Opción 2** (clean + migrate), o  
- Ejecutar a mano los `INSERT` de las migraciones que quieras repetir (solo en desarrollo).

---

## Opción 2: Borrar todo y recrear (Flyway clean + migrate)

Elimina **todas** las tablas y objetos creados por Flyway y vuelve a aplicar todas las migraciones desde cero. La base queda como recién creada, con seeds incluidos.

### Requisito

Flyway tiene `clean` deshabilitado por defecto en producción. Para usarlo en **desarrollo**:

1. En `application.yml` (o en un perfil `application-dev.yml`) habilita clean:

```yaml
spring:
  flyway:
    enabled: true
    clean-disabled: false   # Solo en desarrollo
    locations: classpath:db/migration
```

2. Ejecutar clean y luego migrate.

### Desde línea de comandos (Maven)

```bash
cd enterprise-service-administration

# Opción A: Solo Flyway (sin arrancar la app)
mvn flyway:clean   -Dflyway.url=jdbc:postgresql://localhost:5432/TU_BASE -Dflyway.user=TU_USER -Dflyway.password=TU_PASSWORD
mvn flyway:migrate -Dflyway.url=jdbc:postgresql://localhost:5432/TU_BASE -Dflyway.user=TU_USER -Dflyway.password=TU_PASSWORD
```

Sustituye `TU_BASE`, `TU_USER`, `TU_PASSWORD` por los valores de tu `DB_URL` / `DB_USERNAME` / `DB_PASSWORD`.

### Desde la aplicación (solo desarrollo)

Si en tu `application-dev.yml` tienes `clean-disabled: false` y arrancas la app, Flyway no hace `clean` solo por arrancar; tienes que invocar `clean` explícitamente (p. ej. con `mvn flyway:clean` como arriba). No se recomienda tener un endpoint o botón que ejecute `clean` en producción.

---

## Recomendación

- **Solo quieres datos en blanco y seguir usando la misma BD:** usa la **Opción 1** (script `purge-data.sql`).
- **Quieres dejar la BD como recién creada con todos los seeds:** usa la **Opción 2** (Flyway clean + migrate), solo en desarrollo y con `clean-disabled: false`.

Si me dices si usas solo script SQL o también Maven/Flyway, puedo ajustar los comandos a tu flujo (por ejemplo, usando las mismas variables que `application.yml`).
