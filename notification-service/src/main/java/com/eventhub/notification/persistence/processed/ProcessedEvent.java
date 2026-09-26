package com.eventhub.notification.persistence.processed;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_events")
public class ProcessedEvent {

    @Id
    @Column(name = "message_id", nullable = false)
    private UUID messageId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(
            name = "processed_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private Instant processedAt;

    protected ProcessedEvent() {
    }

    public ProcessedEvent(
            UUID messageId,
            String eventType,
            UUID aggregateId
    ) {
        this.messageId = messageId;
        this.eventType = eventType;
        this.aggregateId = aggregateId;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public String getEventType() {
        return eventType;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }
}