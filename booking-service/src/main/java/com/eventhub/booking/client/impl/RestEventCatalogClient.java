package com.eventhub.booking.client.impl;

import com.eventhub.booking.client.EventCatalogClient;
import com.eventhub.booking.client.dto.EventInfo;
import com.eventhub.booking.client.dto.TicketTypeInfo;
import com.eventhub.booking.exception.EventCatalogNotFoundException;
import com.eventhub.booking.exception.EventCatalogUnavailableException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Component
public class RestEventCatalogClient implements EventCatalogClient {

    private final RestClient restClient;

    public RestEventCatalogClient(
            @LoadBalanced RestClient.Builder restClientBuilder
    ) {
        this.restClient = restClientBuilder.build();
    }

    @Override
    @Retry(name = "eventCatalog")
    @CircuitBreaker(name = "eventCatalog")
    public TicketTypeInfo getTicketTypeInfo(
            UUID eventId,
            UUID ticketTypeId
    ) {

        try {
            return restClient
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

            throw new EventCatalogUnavailableException(
                    "Event service is unavailable"
            );

        } catch (IllegalStateException ex) {

            throw new EventCatalogUnavailableException(
                    "Event service is unavailable"
            );

        } catch (RestClientException ex) {

            throw new EventCatalogUnavailableException(
                    "Failed to communicate with event service"
            );
        }
    }


    @Override
    @Retry(name = "eventCatalog")
    @CircuitBreaker(name = "eventCatalog")
    public EventInfo getEventInfo(UUID eventId) {

        try {
            return restClient
                    .get()
                    .uri(
                            "http://EVENT-SERVICE/api/events/{eventId}",
                            eventId
                    )
                    .retrieve()
                    .body(EventInfo.class);

        } catch (HttpClientErrorException.NotFound ex) {

            throw new EventCatalogNotFoundException(
                    "Event not found with id: " + eventId
            );

        } catch (ResourceAccessException ex) {

            throw new EventCatalogUnavailableException(
                    "Event service is unavailable"
            );

        } catch (IllegalStateException ex) {

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