# Tablas de productos y datos relacionados

## Origen de los datos

| Tabla | Dónde se llenan |
|-------|------------------|
| **products** | Al crear/editar productos por API (POST/PATCH `/api/apps/products`) o al importar Excel desde el frontend. |
| **product_prices** | Al crear o actualizar un producto enviando el array `prices` en el body (cada elemento con `priceListId` y `price`). El backend persiste cada precio explícitamente. |
| **price_lists** | Migraciones Flyway: `V4__seed_clients.sql` (Público, Mayoreo, Proveedores) y `V5__products_categories.sql` (Super Mayoreo, VIP). También se crean al llamar `POST /api/apps/clients/price-lists` con `{ "name": "Nombre lista" }` (p. ej. al importar Excel con columnas de precio nuevas). |
| **locations** | Migración `V6__seed_products.sql` inserta al menos una ubicación (Sucursal Principal). También se pueden crear por API si existe endpoint de ubicaciones. |
| **product_location_stock** | Al crear o actualizar un producto enviando el array `locationStocks` (producto + ubicación + cantidad / mínimo). Si nunca se envían, la tabla queda vacía. |
| **product_variants** | Al crear o actualizar un producto enviando el array `variants` (SKU, cantidad, opciones). La importación Excel no envía variantes; solo se usan desde el formulario de edición. |
| **product_variant_options** | Se llenan junto con cada variante (opción nombre/valor). |

## Si ves tablas vacías

1. **product_prices vacía**  
   - Asegúrate de que el backend tenga el cambio que usa `ProductPriceRepository.saveAndFlush(pe)` al crear/actualizar productos.  
   - Al importar Excel, el frontend debe enviar `prices` con `priceListId` válido (UUID de `price_lists`).  
   - Comprueba que en `price_lists` existan filas (migraciones V4 y V5).

2. **locations vacía**  
   - Ejecuta las migraciones Flyway (arrancar la app con Flyway habilitado).  
   - `V6__seed_products.sql` inserta la ubicación "Sucursal Principal".

3. **price_lists vacía**  
   - Ejecuta migraciones: V4 y V5 insertan listas de precios.  
   - O crea listas por API: `POST /api/apps/clients/price-lists` con `{ "name": "Precio Público" }` (etc.).

4. **product_location_stock / product_variants / product_variant_options vacías**  
   - Es normal si solo creas productos por importación Excel (no envían stock por ubicación ni variantes).  
   - Se llenan cuando creas o editas un producto desde el formulario con “Stock por ubicación” o “Variantes”.

## Migraciones relevantes

- `V4__seed_clients.sql`: inserciones en `price_lists`.
- `V5__products_categories.sql`: creación de tablas y más `price_lists`.
- `V6__seed_products.sql`: inserciones en `locations`, `categories`, `brands`, `products`, `product_prices`, `product_location_stock`, `stock_movements`.

Si la base se creó sin ejecutar todas las migraciones, ejecuta Flyway (o aplica los scripts a mano) para tener datos de ejemplo en `locations` y `price_lists`.
