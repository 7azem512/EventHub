package com.eventhub.notification.persistence.processed;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ProcessedEventRepository
        extends JpaRepository<ProcessedEvent, UUID> {

    @Modifying
    @Query(value = """
            INSERT INTO processed_events (
                message_id,
                event_type,
                aggregate_id
            )
            VALUES (
                :messageId,
                :eventType,
                :aggregateId
            )
            ON CONFLICT (message_id) DO NOTHING
            """,
            nativeQuery = true)
    int insertIfAbsent(
            @Param("messageId") UUID messageId,
            @Param("eventType") String eventType,
            @Param("aggregateId") UUID aggregateId
    );
}