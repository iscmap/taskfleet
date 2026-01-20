CREATE TABLE IF NOT EXISTS outbox_events (
                                             id UUID PRIMARY KEY,
                                             event_type VARCHAR(100) NOT NULL,
    payload_json TEXT NOT NULL,
    correlation_id VARCHAR(100) NULL,
    occurred_at TIMESTAMP NOT NULL,
    published_at TIMESTAMP NULL
    );

CREATE INDEX IF NOT EXISTS idx_outbox_unpublished ON outbox_events(published_at) WHERE published_at IS NULL;
