CREATE TABLE IF NOT EXISTS processed_events (
                                                id UUID PRIMARY KEY,
                                                event_id UUID NOT NULL UNIQUE,
                                                processed_at TIMESTAMP NOT NULL DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_processed_events_event_id ON processed_events(event_id);
