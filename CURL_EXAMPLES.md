# Curl para probar las APIs

Reemplaza `YOUR_JWT_TOKEN` por tu Bearer token (login Keycloak). Base URL: `http://localhost:8080`.

---

## Clientes (`/api/apps/clients`)

### Listar todos (sin resumen)
```bash
curl -s -X GET "http://localhost:8080/api/apps/clients" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Listar todos (con resumen: total vendido, deuda, últimas ventas)
```bash
curl -s -X GET "http://localhost:8080/api/apps/clients?summary=true" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Buscar clientes
```bash
curl -s -X GET "http://localhost:8080/api/apps/clients/search?q=Alexander" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Obtener cliente por ID (con resumen)
```bash
curl -s -X GET "http://localhost:8080/api/apps/clients/b2000000-0000-0000-0000-000000000001" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Últimas ventas de un cliente
```bash
curl -s -X GET "http://localhost:8080/api/apps/clients/b2000000-0000-0000-0000-000000000001/sales?limit=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Listas de precios (clientes)
```bash
curl -s -X GET "http://localhost:8080/api/apps/clients/price-lists" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Crear cliente
```bash
curl -s -X POST "http://localhost:8080/api/apps/clients" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Juan\",
    \"lastName\": \"Pérez\",
    \"phone\": \"+525512345678\",
    \"email\": \"juan@test.com\",
    \"comments\": \"Cliente nuevo\",
    \"clientType\": \"CONSUMER\",
    \"addresses\": [
      {
        \"addressType\": \"MAIN\",
        \"street\": \"Av. Principal\",
        \"exteriorNumber\": \"100\",
        \"postalCode\": \"01000\",
        \"colonia\": \"Centro\",
        \"city\": \"CDMX\",
        \"state\": \"CDMX\",
        \"country\": \"México\"
      }
    ]
  }"
```

### Actualizar cliente
```bash
curl -s -X PATCH "http://localhost:8080/api/apps/clients/b2000000-0000-0000-0000-000000000001" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Alexander\",
    \"lastName\": \"Stum Comple May\",
    \"phone\": \"+525548294866\",
    \"email\": \"munchalec@gmail.com\",
    \"comments\": \"Cliente de Mayoreo\",
    \"clientType\": \"CONSUMER\",
    \"addresses\": [
      {
        \"addressType\": \"MAIN\",
        \"street\": \"Nicolas Bravo\",
        \"exteriorNumber\": \"98\",
        \"postalCode\": \"55606\",
        \"colonia\": \"Barrio San Marcos\",
        \"municipio\": \"Zupango\",
        \"city\": \"Zupango\",
        \"state\": \"Estado de México\",
        \"country\": \"México\"
      }
    ]
  }"
```

### Eliminar cliente
```bash
# Usa un UUID de un cliente que hayas creado para no borrar datos de seed
curl -s -X DELETE "http://localhost:8080/api/apps/clients/CLIENT_UUID_AQUI" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Categorías (`/api/apps/categories`)

### Listar todas
```bash
curl -s -X GET "http://localhost:8080/api/apps/categories" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Buscar categorías
```bash
curl -s -X GET "http://localhost:8080/api/apps/categories/search?name=Quema" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Obtener por ID
```bash
curl -s -X GET "http://localhost:8080/api/apps/categories/f7000000-0000-0000-0000-000000000001" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Crear categoría
```bash
curl -s -X POST "http://localhost:8080/api/apps/categories" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Nueva Categoría\"}"
```

### Actualizar categoría
```bash
curl -s -X PATCH "http://localhost:8080/api/apps/categories/f7000000-0000-0000-0000-000000000001" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Quemadores Actualizado\"}"
```

### Eliminar categoría
```bash
# Solo si no hay productos usando esta categoría
curl -s -X DELETE "http://localhost:8080/api/apps/categories/CATEGORY_UUID_AQUI" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Marcas (`/api/apps/brands`)

### Listar todas
```bash
curl -s -X GET "http://localhost:8080/api/apps/brands" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Buscar marcas
```bash
curl -s -X GET "http://localhost:8080/api/apps/brands/search?name=Mesofrance" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Obtener por ID
```bash
curl -s -X GET "http://localhost:8080/api/apps/brands/f8000000-0000-0000-0000-000000000001" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Crear marca
```bash
curl -s -X POST "http://localhost:8080/api/apps/brands" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Nueva Marca\"}"
```

### Actualizar marca
```bash
curl -s -X PATCH "http://localhost:8080/api/apps/brands/f8000000-0000-0000-0000-000000000001" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"name\": \"Mesofrance Labs\"}"
```

### Eliminar marca
```bash
curl -s -X DELETE "http://localhost:8080/api/apps/brands/BRAND_UUID_AQUI" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Ubicaciones (`/api/apps/locations`)

### Listar todas
```bash
curl -s -X GET "http://localhost:8080/api/apps/locations" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Obtener por ID
```bash
curl -s -X GET "http://localhost:8080/api/apps/locations/f6000000-0000-0000-0000-000000000001" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

---

## Productos (`/api/apps/products`)

### Listar todos (con precios y stock)
```bash
curl -s -X GET "http://localhost:8080/api/apps/products?pricesAndStock=true" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Buscar productos (nombre, SKU o código de barras)
```bash
curl -s -X GET "http://localhost:8080/api/apps/products/search?q=Acelerador" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Obtener producto por ID (detalle completo: precios, stock, variantes, últimos movimientos)
```bash
curl -s -X GET "http://localhost:8080/api/apps/products/f9000000-0000-0000-0000-000000000001" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Últimos movimientos de un producto
```bash
curl -s -X GET "http://localhost:8080/api/apps/products/f9000000-0000-0000-0000-000000000001/movements?limit=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Crear producto (con categoría por nombre, marca por nombre, precios y stock)
```bash
curl -s -X POST "http://localhost:8080/api/apps/products" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Jabón de mano\",
    \"sku\": \"JAB-01\",
    \"productType\": \"PRODUCT\",
    \"categoryName\": \"Higiene\",
    \"brandName\": \"Genérico\",
    \"unitOfSale\": \"Unidad\",
    \"description\": \"Jabón líquido 500ml\",
    \"useStock\": true,
    \"chargeTax\": true,
    \"ivaRate\": 16,
    \"costNet\": 30.00,
    \"includeInCatalog\": true,
    \"sellAtPos\": true,
    \"prices\": [
      {\"priceListId\": \"a1000000-0000-0000-0000-000000000001\", \"price\": 80.00, \"currency\": \"MXN\"},
      {\"priceListId\": \"a1000000-0000-0000-0000-000000000002\", \"price\": 65.00, \"currency\": \"MXN\"}
    ],
    \"locationStocks\": [
      {\"locationId\": \"f6000000-0000-0000-0000-000000000001\", \"quantity\": 50, \"minQuantity\": 5}
    ]
  }"
```

### Crear producto con categoría/marca por ID
```bash
curl -s -X POST "http://localhost:8080/api/apps/products" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Producto Test\",
    \"productType\": \"PRODUCT\",
    \"categoryId\": \"f7000000-0000-0000-0000-000000000001\",
    \"brandId\": \"f8000000-0000-0000-0000-000000000001\",
    \"unitOfSale\": \"Unidad\",
    \"useStock\": true,
    \"chargeTax\": true,
    \"costNet\": 100.00,
    \"prices\": [
      {\"priceListId\": \"a1000000-0000-0000-0000-000000000001\", \"price\": 200.00}
    ],
    \"locationStocks\": [
      {\"locationId\": \"f6000000-0000-0000-0000-000000000001\", \"quantity\": 10, \"minQuantity\": 0}
    ]
  }"
```

### Actualizar producto
```bash
curl -s -X PATCH "http://localhost:8080/api/apps/products/f9000000-0000-0000-0000-000000000001" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Acelerador Metabolico Mesofrance\",
    \"sku\": \"ACM-MES-01\",
    \"productType\": \"PRODUCT\",
    \"categoryId\": \"f7000000-0000-0000-0000-000000000001\",
    \"brandId\": \"f8000000-0000-0000-0000-000000000001\",
    \"unitOfSale\": \"Capsulas\",
    \"description\": \"Acelerador Metabolico Capsulas marca Mesofrance\",
    \"useStock\": true,
    \"chargeTax\": true,
    \"ivaRate\": 16,
    \"costNet\": 250.00,
    \"costWithTax\": 250.00,
    \"includeInCatalog\": true,
    \"sellAtPos\": true,
    \"prices\": [
      {\"priceListId\": \"a1000000-0000-0000-0000-000000000001\", \"price\": 575.00},
      {\"priceListId\": \"a1000000-0000-0000-0000-000000000002\", \"price\": 395.00}
    ],
    \"locationStocks\": [
      {\"locationId\": \"f6000000-0000-0000-0000-000000000001\", \"quantity\": 7, \"minQuantity\": 5}
    ]
  }"
```

### Eliminar producto
```bash
# Usa UUID de un producto creado por ti para no borrar datos de seed
curl -s -X DELETE "http://localhost:8080/api/apps/products/PRODUCT_UUID_AQUI" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## Usuarios / Contactos (`/api/apps/contacts`) — ya existente

### Listar usuarios
```bash
curl -s -X GET "http://localhost:8080/api/apps/contacts/all" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### Actualizar usuario (ejemplo con `contact` anidado)
```bash
curl -s -X PATCH "http://localhost:8080/api/apps/contacts/contact" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"id\": \"USER_KEYCLOAK_UUID\",
    \"contact\": {
      \"id\": \"USER_KEYCLOAK_UUID\",
      \"name\": \"Nombre Apellido\",
      \"emails\": [{\"email\": \"user@test.com\", \"label\": \"personal\"}],
      \"roles\": [\"ADMIN\"]
    }
  }"
```

---

## Cómo obtener el JWT

1. Inicia sesión en Keycloak (realm `hvati`) y copia el token de acceso, o  
2. Usa el flujo de recursos (client credentials / password) de Keycloak y guarda el `access_token` en una variable:

```bash
# Ejemplo (ajusta URL, realm, client_id, client_secret o user/pass según tu Keycloak)
TOKEN=$(curl -s -X POST "https://auth-develop.hvati.cloud/realms/hvati/protocol/openid-connect/token" \
  -d "client_id=hvati-backend" \
  -d "client_secret=TU_CLIENT_SECRET" \
  -d "username=radiaz@hvati.test" \
  -d "password=TU_PASSWORD" \
  -d "grant_type=password" | jq -r '.access_token')

# Luego usa en los curl:
curl -s -X GET "http://localhost:8080/api/apps/clients" -H "Authorization: Bearer $TOKEN"
```

UUIDs de referencia del seed:
- Cliente Alexander: `b2000000-0000-0000-0000-000000000001`
- Lista precios Público: `a1000000-0000-0000-0000-000000000001`
- Categoría Quemadores: `f7000000-0000-0000-0000-000000000001`
- Marca Mesofrance: `f8000000-0000-0000-0000-000000000001`
- Ubicación Sucursal Principal: `f6000000-0000-0000-0000-000000000001`
- Producto Acelerador: `f9000000-0000-0000-0000-000000000001`
