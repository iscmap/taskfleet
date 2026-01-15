CREATE TABLE IF NOT EXISTS idempotency_keys (
                                                id UUID PRIMARY KEY,
                                                idempotency_key VARCHAR(128) NOT NULL UNIQUE,
    request_hash VARCHAR(64) NOT NULL,
    order_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_idempotency_order_id ON idempotency_keys(order_id);
