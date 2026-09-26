CREATE TABLE processed_events (
                                  message_id UUID PRIMARY KEY,
                                  event_type VARCHAR(100) NOT NULL,
                                  aggregate_id UUID NOT NULL,
                                  processed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_processed_events_processed_at
    ON processed_events (processed_at);