package com.eventhub.event.services;

import com.eventhub.event.dto.request.CreateEventRequest;
import com.eventhub.event.dto.request.UpdateEventRequest;
import com.eventhub.event.dto.response.EventResponse;
import com.eventhub.event.enums.EventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.UUID;

public interface EventService {
    EventResponse createEvent(CreateEventRequest createEventRequest, UUID organizerId);
    EventResponse updateEvent(UUID eventId, UpdateEventRequest request, UUID currentUserId, boolean admin);
    EventResponse getEventById(UUID eventId);
    Page<EventResponse> getAllEvents(Pageable pageable);
    Page<EventResponse> getEventByStatus(EventStatus status, Pageable pageable);
    Page<EventResponse> getEventByOrganizer(UUID organizerId, Pageable pageable);
    Page<EventResponse> getEventByCategory(UUID categoryId, Pageable pageable);
    Page<EventResponse> searchEvents(String search, Pageable pageable);
    void deleteEvent(UUID eventId, UUID currentUserId, boolean admin);
    EventResponse submitEvent(UUID eventId, UUID currentUserId,boolean admin);
    EventResponse approveEvent(UUID eventId);
    EventResponse rejectEvent(UUID eventId);
    EventResponse reviseEvent(UUID eventId, UUID currentUserId, boolean admin);
    EventResponse cancelEvent(UUID eventId, UUID currentUserId, boolean admin);
    EventResponse completeEvent(UUID eventId);

}
