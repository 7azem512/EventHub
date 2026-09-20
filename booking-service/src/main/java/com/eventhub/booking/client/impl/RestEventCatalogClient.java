package com.eventhub.booking.client.impl;

import com.eventhub.booking.client.EventCatalogClient;
import com.eventhub.booking.client.dto.EventInfo;
import com.eventhub.booking.client.dto.TicketTypeInfo;

import com.eventhub.booking.exception.EventCatalogNotFoundException;
import com.eventhub.booking.exception.EventCatalogUnavailableException;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;

import org.springframework.stereotype.Component;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;


import java.util.UUID;

@Component
public class RestEventCatalogClient implements EventCatalogClient {

    private final RestClient.Builder restClientBuilder;

    public RestEventCatalogClient(
            @LoadBalanced RestClient.Builder restClientBuilder
    ) {
        this.restClientBuilder = restClientBuilder;
    }

    @Override
    public TicketTypeInfo getTicketTypeInfo(
            UUID eventId,
            UUID ticketTypeId
    ) {

        try {
            return restClientBuilder
                    .build()
                    .get()
                    .uri(
                            "http://EVENT-SERVICE/api/events/{eventId}/ticket-types/{ticketTypeId}",
                            eventId,
                            ticketTypeId
                    )
                    .retrieve()
                    .body(TicketTypeInfo.class);

        } catch (HttpClientErrorException.NotFound ex) {

            throw new EventCatalogNotFoundException(
                    "Event or ticket type not found"
            );

        } catch (ResourceAccessException ex) {

            throw new EventCatalogUnavailableException("Event service is unavailable");

        } catch (RestClientException ex) {

            throw new EventCatalogUnavailableException("Failed to communicate with event service");
        }
    }


    @Override
    public EventInfo getEventInfo(UUID eventId) {

        try {
            return restClientBuilder
                    .build()
                    .get()
                    .uri("http://EVENT-SERVICE/api/events/{eventId}", eventId)
                    .retrieve()
                    .body(EventInfo.class);

        } catch (HttpClientErrorException.NotFound ex) {

            throw new EventCatalogNotFoundException("Event not found with id: " + eventId);

        } catch (ResourceAccessException ex) {

            throw new EventCatalogUnavailableException(
                    "Event service is unavailable"
            );

        } catch (RestClientException ex) {

            throw new EventCatalogUnavailableException(
                    "Failed to communicate with event service"
            );
        }
    }


}