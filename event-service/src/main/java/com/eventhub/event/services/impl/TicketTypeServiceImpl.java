package com.eventhub.event.services.impl;

import com.eventhub.event.dto.request.CreateTicketTypeRequest;
import com.eventhub.event.dto.request.UpdateTicketTypeRequest;
import com.eventhub.event.dto.response.TicketTypeResponse;
import com.eventhub.event.entity.Event;
import com.eventhub.event.entity.TicketType;
import com.eventhub.event.enums.EventStatus;
import com.eventhub.event.exception.BusinessRuleException;
import org.springframework.security.access.AccessDeniedException;
import com.eventhub.event.exception.DuplicateResourceException;
import com.eventhub.event.exception.ResourceNotFoundException;
import com.eventhub.event.mapper.TicketTypeMapper;
import com.eventhub.event.repository.EventRepository;
import com.eventhub.event.repository.TicketTypeRepository;
import com.eventhub.event.services.TicketTypeService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketTypeServiceImpl implements TicketTypeService {
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketTypeMapper ticketTypeMapper;
    private final EventRepository eventRepository;



    @Override
    public TicketTypeResponse createTicketType(UUID eventId, CreateTicketTypeRequest request, UUID currentUserId, boolean admin) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        validateEventOwnership(event, currentUserId, admin);
        validateEventStatus(event);
        TicketType ticketType = ticketTypeMapper.toEntity(request, event);
        if (ticketTypeRepository.existsByEventIdAndNameIgnoreCase(eventId,request.getName())){
            throw new DuplicateResourceException("Ticket type already exists");
        }
        ticketTypeRepository.save(ticketType);
        return ticketTypeMapper.toResponse(ticketType);
    }

    @Override
    public TicketTypeResponse updateTicketType(UUID eventId, UUID ticketTypeId, UpdateTicketTypeRequest request, UUID currentUserId, boolean admin) {
        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket type not found"));
        if (!ticketType.getEvent().getId().equals(eventId)){
            throw new ResourceNotFoundException("Ticket type not found for this event");
        }
        validateEventOwnership(ticketType.getEvent(), currentUserId, admin);
        validateEventStatus(ticketType.getEvent());

        if (
                !ticketType.getName().equalsIgnoreCase(request.getName())
                        &&
                        ticketTypeRepository.existsByEventIdAndNameIgnoreCase(eventId, request.getName())
        ) {
            throw new DuplicateResourceException("Ticket type already exists for this event");
        }
        ticketTypeMapper.updateEntity(ticketType, request);
        return ticketTypeMapper.toResponse(ticketType);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketTypeResponse getTicketTypeById(UUID eventId, UUID ticketTypeId) {

        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
                        .orElseThrow(() -> new ResourceNotFoundException("Ticket type not found"));

        if (!ticketType.getEvent().getId().equals(eventId)) {
            throw new ResourceNotFoundException("Ticket type not found for this event");
        }

        return ticketTypeMapper.toResponse(ticketType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketTypeResponse> getTicketTypesByEvent(UUID eventId) {

        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found");
        }

        return ticketTypeRepository
                .findAllByEventId(eventId)
                .stream()
                .map(ticketTypeMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteTicketType(UUID eventId, UUID ticketTypeId, UUID currentUserId, boolean admin) {
        TicketType ticketType = ticketTypeRepository.findById(ticketTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket type not found"));

        if (!ticketType.getEvent().getId().equals(eventId)) {
            throw new ResourceNotFoundException("Ticket type not found");
        }
        validateEventOwnership(ticketType.getEvent(), currentUserId, admin);
        validateEventStatus(ticketType.getEvent());

        ticketTypeRepository.delete(ticketType);
    }


    private void validateEventOwnership(Event event, UUID currentUserId, boolean admin) {
        if (!admin && !event.getOrganizerId().equals(currentUserId)) {
            throw new AccessDeniedException("You are not authorized to modify ticket types for this event");
        }}

    private void validateEventStatus(Event event)   {
            if (event.getStatus() != EventStatus.DRAFT) {
                throw new BusinessRuleException("Event is not in draft status");
            }
        }
    }

