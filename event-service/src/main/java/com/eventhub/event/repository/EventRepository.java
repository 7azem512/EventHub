package com.eventhub.event.repository;

import com.eventhub.event.entity.Event;
import com.eventhub.event.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    Page<Event> findAllByStatus(EventStatus status, Pageable pageable);
    Page<Event> findAllByOrganizerId(UUID organizerId, Pageable pageable);
    Page<Event> findAllByCategoryId(UUID categoryId, Pageable pageable);
    Page<Event> findAllByTitleContainingIgnoreCase(String title, Pageable pageable);
    boolean existsByCategoryId(UUID categoryId);
}
