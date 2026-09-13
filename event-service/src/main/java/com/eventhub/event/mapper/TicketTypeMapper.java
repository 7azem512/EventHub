package com.eventhub.event.mapper;

import com.eventhub.event.dto.request.CreateTicketTypeRequest;
import com.eventhub.event.dto.request.UpdateTicketTypeRequest;
import com.eventhub.event.dto.response.TicketTypeResponse;
import com.eventhub.event.entity.Event;
import com.eventhub.event.entity.TicketType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class TicketTypeMapper {

    public TicketType toEntity(CreateTicketTypeRequest createTicketTypeRequest, Event event){
        return TicketType.builder()
                .name(createTicketTypeRequest.getName())
                .price(createTicketTypeRequest.getPrice())
                .capacity(createTicketTypeRequest.getCapacity())
                .event(event)
                .build();
    }
    public void updateEntity(TicketType ticketType, UpdateTicketTypeRequest updateTicketTypeRequest){
        ticketType.setName(updateTicketTypeRequest.getName());
        ticketType.setPrice(updateTicketTypeRequest.getPrice());
        ticketType.setCapacity(updateTicketTypeRequest.getCapacity());
    }
    public TicketTypeResponse toResponse(TicketType ticketType){
        return TicketTypeResponse.builder()
                .id(ticketType.getId())
                .name(ticketType.getName())
                .price(ticketType.getPrice())
                .capacity(ticketType.getCapacity())
                .eventId(ticketType.getEvent().getId())
                .createdAt(ticketType.getCreatedAt())
                .updatedAt(ticketType.getUpdatedAt())
                .build();
    }
}
