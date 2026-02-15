-- Listas de precios adicionales para productos (Público, Mayoreo, Super Mayoreo, VIP)
INSERT INTO price_lists (id, name, description) VALUES
('a1000000-0000-0000-0000-000000000004', 'Super Mayoreo', 'Precio super mayoreo'),
('a1000000-0000-0000-0000-000000000005', 'VIP', 'Precio VIP');

-- Categorías de productos
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Marcas
CREATE TABLE brands (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL
);

CREATE INDEX idx_brands_name ON brands(name);

-- Ubicaciones / Sucursales (stock por ubicación)
CREATE TABLE locations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL
);

-- Productos
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    sku VARCHAR(100),
    barcode VARCHAR(100),
    product_type VARCHAR(20) NOT NULL DEFAULT 'PRODUCT',
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    brand_id UUID REFERENCES brands(id) ON DELETE SET NULL,
    unit_of_sale VARCHAR(50) DEFAULT 'Unidad',
    location_text VARCHAR(255),
    description TEXT,
    use_stock BOOLEAN DEFAULT true,
    use_lots_expiry BOOLEAN DEFAULT false,
    charge_tax BOOLEAN DEFAULT true,
    iva_rate NUMERIC(5, 2) DEFAULT 16.00,
    ieps_rate NUMERIC(5, 2) DEFAULT 0,
    cost_net NUMERIC(15, 2),
    cost_with_tax NUMERIC(15, 2),
    include_in_catalog BOOLEAN DEFAULT true,
    sell_at_pos BOOLEAN DEFAULT true,
    require_prescription BOOLEAN DEFAULT false,
    allow_manufacturing BOOLEAN DEFAULT false,
    sat_key VARCHAR(20),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

COMMENT ON COLUMN products.product_type IS 'PRODUCT, SERVICE';
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_barcode ON products(barcode);
CREATE INDEX idx_products_category ON products(category_id);

-- Precio por producto y lista de precios (márgen/ganancia se calculan desde costo)
CREATE TABLE product_prices (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    price_list_id UUID NOT NULL REFERENCES price_lists(id) ON DELETE CASCADE,
    price NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'MXN',
    UNIQUE(product_id, price_list_id)
);

CREATE INDEX idx_product_prices_product ON product_prices(product_id);

-- Stock por producto y ubicación (existencias y cantidad mínima por sucursal)
CREATE TABLE product_location_stock (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    location_id UUID NOT NULL REFERENCES locations(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL DEFAULT 0,
    min_quantity INTEGER NOT NULL DEFAULT 0,
    UNIQUE(product_id, location_id)
);

CREATE INDEX idx_product_location_stock_product ON product_location_stock(product_id);

-- Variantes de producto (tallas, colores, etc.)
CREATE TABLE product_variants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    sku VARCHAR(100),
    quantity INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE product_variant_options (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    variant_id UUID NOT NULL REFERENCES product_variants(id) ON DELETE CASCADE,
    option_name VARCHAR(100) NOT NULL,
    option_value VARCHAR(255) NOT NULL
);

CREATE INDEX idx_product_variants_product ON product_variants(product_id);

-- Movimientos de stock (últimos movimientos, auditoría)
CREATE TABLE stock_movements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    location_id UUID NOT NULL REFERENCES locations(id) ON DELETE CASCADE,
    movement_type VARCHAR(20) NOT NULL,
    quantity INTEGER NOT NULL,
    previous_quantity INTEGER NOT NULL,
    new_quantity INTEGER NOT NULL,
    reference_type VARCHAR(50),
    reference_id UUID,
    user_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT NOW()
);

COMMENT ON COLUMN stock_movements.movement_type IS 'IN, OUT';
COMMENT ON COLUMN stock_movements.quantity IS 'Positivo entrada, negativo salida';
COMMENT ON COLUMN stock_movements.reference_type IS 'SALE, PURCHASE, ADJUSTMENT, CREATION';
CREATE INDEX idx_stock_movements_product ON stock_movements(product_id);
CREATE INDEX idx_stock_movements_created ON stock_movements(created_at DESC);
