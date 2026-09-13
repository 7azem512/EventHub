package com.eventhub.event.repository;

import com.eventhub.event.entity.TicketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface TicketTypeRepository extends JpaRepository<TicketType, UUID> {
    List<TicketType> findAllByEventId(UUID eventId);
    boolean existsByEventIdAndNameIgnoreCase(UUID eventId, String name);
    boolean existsByEventId(UUID eventId);
}
