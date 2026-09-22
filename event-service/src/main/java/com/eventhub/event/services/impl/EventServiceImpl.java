package com.eventhub.event.services.impl;

import com.eventhub.event.dto.request.CreateEventRequest;
import com.eventhub.event.dto.request.UpdateEventRequest;
import com.eventhub.event.dto.response.EventResponse;
import com.eventhub.event.entity.Category;
import com.eventhub.event.entity.Event;
import com.eventhub.event.enums.EventStatus;
import com.eventhub.event.exception.BusinessRuleException;
import com.eventhub.event.exception.ResourceNotFoundException;
import com.eventhub.event.mapper.EventMapper;
import com.eventhub.event.repository.CategoryRepository;
import com.eventhub.event.repository.EventRepository;
import com.eventhub.event.repository.TicketTypeRepository;
import com.eventhub.event.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final TicketTypeRepository ticketTypeRepository;
    @Override
    public EventResponse createEvent(CreateEventRequest createEventRequest, UUID organizerId) {
        validateEventDates(createEventRequest.getStartDate(),
                createEventRequest.getEndDate(),
                createEventRequest.getBookingStartDate(),
                createEventRequest.getBookingEndDate());
        Category category = categoryRepository.findById(createEventRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Event event = eventMapper.toEntity(createEventRequest, category, organizerId);
        eventRepository.save(event);
        return eventMapper.toResponse(event);
    }

    @Override
    public EventResponse updateEvent(UUID eventId, UpdateEventRequest updateEventRequest, UUID currentUserId, boolean admin) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        validateOwnership(event, currentUserId, admin);
        if (event.getStatus()!=EventStatus.DRAFT){
            throw new BusinessRuleException("Event is not in draft status");
        }
        validateEventDates(updateEventRequest.getStartDate(), updateEventRequest.getEndDate(), updateEventRequest.getBookingStartDate(), updateEventRequest.getBookingEndDate());
        Category category=categoryRepository.findById(updateEventRequest.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        eventMapper.updateEntity(event, updateEventRequest, category);
        return eventMapper.toResponse(event);
    }

    @Override
    @Transactional(readOnly = true)
    public EventResponse getEventById(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return eventMapper.toResponse(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getAllEvents(Pageable pageable) {

        return eventRepository.findAll(pageable).map(eventMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getEventByStatus(EventStatus status, Pageable pageable) {
        return eventRepository
                .findAllByStatus(status, pageable)
                .map(eventMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getEventByOrganizer(UUID organizerId, Pageable pageable) {
        return eventRepository.findAllByOrganizerId(organizerId, pageable).map(eventMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> getEventByCategory(UUID categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found");
        }
        return eventRepository.findAllByCategoryId(categoryId, pageable).map(eventMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventResponse> searchEvents(String search, Pageable pageable) {
        return eventRepository.findAllByTitleContainingIgnoreCase(search, pageable).map(eventMapper::toResponse);
    }

    @Override
    public void deleteEvent(UUID eventId, UUID currentUserId, boolean admin) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        validateOwnership(event, currentUserId, admin);
        if (event.getStatus()!=EventStatus.DRAFT&&event.getStatus()!=EventStatus.REJECTED){
            throw new BusinessRuleException("Only DRAFT or REJECTED events can be deleted");
        }
        if (ticketTypeRepository.existsByEventId(eventId)) {
            throw new BusinessRuleException("Cannot delete event with associated ticket types");
        }
        eventRepository.delete(event);
    }


    @Override
    public EventResponse submitEvent(UUID eventId, UUID currentUserId, boolean admin) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        validateOwnership(event, currentUserId, admin);
        if (event.getStatus() != EventStatus.DRAFT) {
            throw new BusinessRuleException("Only DRAFT events can be submitted for approval");
        }

        if (!ticketTypeRepository.existsByEventId(eventId)) {
            throw new BusinessRuleException("Event must have at least one ticket type before submission");
        }

        event.setStatus(EventStatus.PENDING_APPROVAL);
        return eventMapper.toResponse(event);
    }


    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public EventResponse approveEvent(UUID eventId) {
        Event event= eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        if (event.getStatus()!=EventStatus.PENDING_APPROVAL){
            throw new BusinessRuleException("Only PENDING_APPROVAL events can be approved");
        }
        event.setStatus(EventStatus.PUBLISHED);
        return eventMapper.toResponse(event);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public EventResponse rejectEvent(UUID eventId) {
        Event event= eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: "+eventId));
        if (event.getStatus()!=EventStatus.PENDING_APPROVAL){
            throw new BusinessRuleException("Only PENDING_APPROVAL events can be rejected");
        }
        event.setStatus(EventStatus.REJECTED);
        return eventMapper.toResponse(event);
    }

    @Override
    public EventResponse reviseEvent(UUID eventId, UUID currentUserId, boolean admin) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        validateOwnership(event, currentUserId, admin);
        if (event.getStatus() != EventStatus.REJECTED) {
            throw new BusinessRuleException("Only REJECTED events can be revised");
        }
        event.setStatus(EventStatus.DRAFT);
        return eventMapper.toResponse(event);
    }


    @Override
    public EventResponse cancelEvent(UUID eventId, UUID currentUserId, boolean admin) {
        Event event =eventRepository.findById(eventId)
                .orElseThrow(()->new ResourceNotFoundException("event not found with id: "+eventId));
        validateOwnership(event,currentUserId,admin);
        if (event.getStatus()!=EventStatus.PUBLISHED){
            throw new BusinessRuleException("Only PUBLISHED events can be cancelled");
        }
        event.setStatus(EventStatus.CANCELLED);
        return eventMapper.toResponse(event);
    }


    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public EventResponse completeEvent(UUID eventId) {
        Event event =eventRepository.findById(eventId)
                .orElseThrow(()->new ResourceNotFoundException("event not found with id: "+eventId));
        if (event.getStatus()!=EventStatus.PUBLISHED){
            throw new BusinessRuleException("Only PUBLISHED events can be completed");
        }
        event.setStatus(EventStatus.COMPLETED);
        return eventMapper.toResponse(event);
    }


    private void validateEventDates(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime bookingStartDate, LocalDateTime bookingEndDate) {
        if (!startDate.isBefore(endDate)) {
            throw new BusinessRuleException(
                    "Event start date must be before event end date"
            );
        }

        if (!bookingStartDate.isBefore(bookingEndDate)) {
            throw new BusinessRuleException(
                    "Booking start date must be before booking end date"
            );
        }

        if (bookingEndDate.isAfter(startDate)) {
            throw new BusinessRuleException(
                    "Booking end date must be before or equal to event start date"
            );
        }
    }

    private void validateOwnership(Event event, UUID currentUserId, boolean admin) {
        if (admin) {
            return;
        }

        if (!event.getOrganizerId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not allowed to modify this event");
        }
    }


}
