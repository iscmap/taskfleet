CREATE TABLE IF NOT EXISTS orders (
                                      id UUID PRIMARY KEY,
                                      customer_email VARCHAR(320) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS order_items (
                                           id UUID PRIMARY KEY,
                                           order_id UUID NOT NULL REFERENCES orders(id),
    sku VARCHAR(64) NOT NULL,
    quantity INT NOT NULL
    );

CREATE INDEX IF NOT EXISTS idx_orders_customer_email ON orders(customer_email);
CREATE INDEX IF NOT EXISTS idx_orders_status ON orders(status);
