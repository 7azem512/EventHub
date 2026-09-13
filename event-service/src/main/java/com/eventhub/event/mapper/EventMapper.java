package com.eventhub.event.mapper;

import com.eventhub.event.dto.request.CreateEventRequest;
import com.eventhub.event.dto.request.UpdateEventRequest;
import com.eventhub.event.dto.response.EventResponse;
import com.eventhub.event.entity.Category;
import com.eventhub.event.entity.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventMapper {
    private final CategoryMapper categoryMapper;

    public Event toEntity(CreateEventRequest createEventRequest,Category category){
        return Event.builder()
                .title(createEventRequest.getTitle())
                .description(createEventRequest.getDescription())
                .location(createEventRequest.getLocation())
                .startDate(createEventRequest.getStartDate())
                .endDate(createEventRequest.getEndDate())
                .bookingStartDate(createEventRequest.getBookingStartDate())
                .bookingEndDate(createEventRequest.getBookingEndDate())
                .organizerId(createEventRequest.getOrganizerId())
                .category(category)
                .build();
    }
    public void updateEntity(Event event,UpdateEventRequest updateEventRequest,Category category){
        event.setTitle(updateEventRequest.getTitle());
        event.setDescription(updateEventRequest.getDescription());
        event.setLocation(updateEventRequest.getLocation());
        event.setStartDate(updateEventRequest.getStartDate());
        event.setEndDate(updateEventRequest.getEndDate());
        event.setBookingStartDate(updateEventRequest.getBookingStartDate());
        event.setBookingEndDate(updateEventRequest.getBookingEndDate());
        event.setCategory(category);


    }
    public EventResponse toResponse(Event event){
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .bookingStartDate(event.getBookingStartDate())
                .bookingEndDate(event.getBookingEndDate())
                .organizerId(event.getOrganizerId())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .category(categoryMapper.toResponse(event.getCategory()))
                .build();
    }
}
