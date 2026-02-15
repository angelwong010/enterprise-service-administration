-- Listas de precios (asignables al cliente)
CREATE TABLE price_lists (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    description VARCHAR(500)
);

-- Clientes (pueden ser consumidor final o proveedor/distribuidor)
CREATE TABLE clients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255),
    phone VARCHAR(50),
    email VARCHAR(255),
    comments TEXT,
    price_list_id UUID REFERENCES price_lists(id) ON DELETE SET NULL,
    credit_limit NUMERIC(15, 2),
    client_type VARCHAR(50) NOT NULL DEFAULT 'CONSUMER',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

COMMENT ON COLUMN clients.credit_limit IS 'NULL = sin limitar';
COMMENT ON COLUMN clients.client_type IS 'CONSUMER, SUPPLIER, DISTRIBUTOR - para segmentación publicidad';

-- Direcciones del cliente (registro, envío, facturación)
CREATE TABLE client_addresses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL REFERENCES clients(id) ON DELETE CASCADE,
    address_type VARCHAR(20) NOT NULL,
    street VARCHAR(255),
    exterior_number VARCHAR(50),
    interior_number VARCHAR(50),
    postal_code VARCHAR(20),
    colonia VARCHAR(255),
    municipio VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    country VARCHAR(100) DEFAULT 'México'
);

COMMENT ON COLUMN client_addresses.address_type IS 'MAIN, SHIPPING, BILLING';

-- Datos de facturación (RFC, razón social, régimen fiscal)
CREATE TABLE client_billing_data (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id UUID NOT NULL UNIQUE REFERENCES clients(id) ON DELETE CASCADE,
    razon_social VARCHAR(500),
    postal_code VARCHAR(20),
    rfc VARCHAR(20),
    regimen_fiscal VARCHAR(255),
    street VARCHAR(255),
    exterior_number VARCHAR(50),
    interior_number VARCHAR(50),
    colonia VARCHAR(255),
    municipio VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255)
);

-- Ventas (para estado de cuenta, total vendido, deuda, segmentación)
CREATE TABLE sales (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sale_number INTEGER NOT NULL,
    client_id UUID NOT NULL REFERENCES clients(id) ON DELETE RESTRICT,
    total NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'MXN',
    payment_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    delivery_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    sale_date TIMESTAMP NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP DEFAULT NOW()
);

COMMENT ON COLUMN sales.payment_status IS 'PAID, PENDING, CANCELLED';
COMMENT ON COLUMN sales.delivery_status IS 'DELIVERED, PENDING, CANCELLED';

-- Índices para consultas y segmentación (publicidad, reportes)
CREATE INDEX idx_clients_client_type ON clients(client_type);
CREATE INDEX idx_clients_email ON clients(email);
CREATE INDEX idx_clients_created_at ON clients(created_at);
CREATE INDEX idx_client_addresses_client_id ON client_addresses(client_id);
CREATE INDEX idx_client_addresses_state ON client_addresses(state);
CREATE INDEX idx_client_addresses_city ON client_addresses(city);
CREATE INDEX idx_sales_client_id ON sales(client_id);
CREATE INDEX idx_sales_sale_date ON sales(sale_date);
CREATE INDEX idx_sales_payment_status ON sales(payment_status);
CREATE UNIQUE INDEX idx_sales_sale_number ON sales(sale_number);
