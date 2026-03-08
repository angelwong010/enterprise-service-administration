-- Permitir borrar clientes aunque tengan ventas: la referencia client_id pasa a ser
-- opcional y al borrar el cliente se pone NULL (las ventas conservan client_name).
ALTER TABLE sales
    DROP CONSTRAINT IF EXISTS sales_client_id_fkey;

ALTER TABLE sales
    ALTER COLUMN client_id DROP NOT NULL;

ALTER TABLE sales
    ADD CONSTRAINT sales_client_id_fkey
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE SET NULL;

COMMENT ON COLUMN sales.client_id IS 'Cliente (opcional). Si se borra el cliente, se pone NULL; la venta conserva client_name.';
