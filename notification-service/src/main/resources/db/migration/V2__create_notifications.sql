CREATE TABLE notifications (
                               id UUID PRIMARY KEY,
                               user_id UUID NOT NULL,

                               type VARCHAR(100) NOT NULL,

                               title VARCHAR(255) NOT NULL,
                               body TEXT NOT NULL,

                               reference_type VARCHAR(100),
                               reference_id UUID,

                               read_at TIMESTAMPTZ NULL,

                               created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_user_created
    ON notifications (user_id, created_at DESC);

CREATE INDEX idx_notifications_user_unread
    ON notifications (user_id)
    WHERE read_at IS NULL;