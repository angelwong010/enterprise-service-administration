-- Extend sales for POS/frontend: subtotal, discount, payment_method, quotation link, addresses (JSONB), status
ALTER TABLE sales
    ADD COLUMN IF NOT EXISTS subtotal NUMERIC(15, 2),
    ADD COLUMN IF NOT EXISTS discount NUMERIC(15, 2) DEFAULT 0,
    ADD COLUMN IF NOT EXISTS payment_method VARCHAR(50),
    ADD COLUMN IF NOT EXISTS quotation_id UUID,
    ADD COLUMN IF NOT EXISTS quotation_number INTEGER,
    ADD COLUMN IF NOT EXISTS client_name VARCHAR(500),
    ADD COLUMN IF NOT EXISTS client_address JSONB,
    ADD COLUMN IF NOT EXISTS shipping_address JSONB,
    ADD COLUMN IF NOT EXISTS channel VARCHAR(50),
    ADD COLUMN IF NOT EXISTS register_id UUID,
    ADD COLUMN IF NOT EXISTS cashier_id UUID,
    ADD COLUMN IF NOT EXISTS salesperson_id UUID,
    ADD COLUMN IF NOT EXISTS invoice_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS invoice_url VARCHAR(500),
    ADD COLUMN IF NOT EXISTS status VARCHAR(30);

COMMENT ON COLUMN sales.status IS 'pendiente, por_enviar, finalizada, cancelled - frontend status';
COMMENT ON COLUMN sales.quotation_id IS 'FK to quotations.id when sale was converted from quotation';

-- Backfill status from payment_status/delivery_status for existing rows
UPDATE sales
SET status = CASE
    WHEN payment_status = 'CANCELLED' THEN 'cancelled'
    WHEN payment_status = 'PAID' AND delivery_status = 'DELIVERED' THEN 'finalizada'
    WHEN payment_status = 'PAID' THEN 'por_enviar'
    ELSE 'pendiente'
END
WHERE status IS NULL;

-- Sale lines (products + shipping line)
CREATE TABLE IF NOT EXISTS sale_lines (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sale_id UUID NOT NULL REFERENCES sales(id) ON DELETE CASCADE,
    product_id UUID,
    product_name VARCHAR(500) NOT NULL,
    is_shipping BOOLEAN NOT NULL DEFAULT FALSE,
    unit_price NUMERIC(15, 4) NOT NULL,
    quantity NUMERIC(12, 4) NOT NULL DEFAULT 1,
    subtotal NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3),
    price_list_id UUID,
    price_list_name VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_sale_lines_sale_id ON sale_lines(sale_id);

-- Sale history (payment, delivery, from_quotation, etc.)
CREATE TABLE IF NOT EXISTS sale_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sale_id UUID NOT NULL REFERENCES sales(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    amount NUMERIC(15, 2),
    payment_method VARCHAR(50),
    quotation_number INTEGER,
    comment TEXT,
    user_id UUID,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_sale_history_sale_id ON sale_history(sale_id);

-- Quotations (sale_id added after table creation to avoid circular FK)
CREATE TABLE IF NOT EXISTS quotations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    number INTEGER NOT NULL,
    client_id UUID REFERENCES clients(id) ON DELETE SET NULL,
    client_name VARCHAR(500),
    status VARCHAR(30) NOT NULL DEFAULT 'draft',
    subtotal NUMERIC(15, 2) NOT NULL DEFAULT 0,
    discount NUMERIC(15, 2) NOT NULL DEFAULT 0,
    total NUMERIC(15, 2) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);
ALTER TABLE quotations ADD COLUMN IF NOT EXISTS sale_id UUID REFERENCES sales(id) ON DELETE SET NULL;

COMMENT ON COLUMN quotations.status IS 'draft, sent, accepted, rejected';
CREATE UNIQUE INDEX IF NOT EXISTS idx_quotations_number ON quotations(number);
CREATE INDEX IF NOT EXISTS idx_quotations_client_id ON quotations(client_id);
CREATE INDEX IF NOT EXISTS idx_quotations_sale_id ON quotations(sale_id);

-- Quotation lines
CREATE TABLE IF NOT EXISTS quotation_lines (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quotation_id UUID NOT NULL REFERENCES quotations(id) ON DELETE CASCADE,
    product_id UUID,
    product_name VARCHAR(500) NOT NULL,
    is_shipping BOOLEAN NOT NULL DEFAULT FALSE,
    unit_price NUMERIC(15, 4) NOT NULL,
    quantity NUMERIC(12, 4) NOT NULL DEFAULT 1,
    subtotal NUMERIC(15, 2) NOT NULL,
    price_list_id UUID,
    price_list_name VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_quotation_lines_quotation_id ON quotation_lines(quotation_id);

-- FK from sales to quotations (quotation_id already added above)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_sales_quotation') THEN
        ALTER TABLE sales ADD CONSTRAINT fk_sales_quotation
            FOREIGN KEY (quotation_id) REFERENCES quotations(id) ON DELETE SET NULL;
    END IF;
EXCEPTION
    WHEN undefined_table THEN NULL;
END $$;
