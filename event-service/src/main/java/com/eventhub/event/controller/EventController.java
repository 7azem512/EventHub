package com.eventhub.event.controller;

import com.eventhub.event.dto.request.CreateEventRequest;
import com.eventhub.event.dto.request.UpdateEventRequest;
import com.eventhub.event.dto.response.EventResponse;
import com.eventhub.event.enums.EventStatus;
import com.eventhub.event.services.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Tag(
        name = "Events",
        description = "Event management APIs"
)
public class EventController {

    private final EventService eventService;


    @PostMapping
    @Operation(
            summary = "Create event",
            description = "Creates a new event"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Event created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Event dates violate business rules"
            )
    })
    public ResponseEntity<EventResponse> create(
            @Valid @RequestBody CreateEventRequest request
    ) {

        EventResponse response =
                eventService.createEvent(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @PutMapping("/{eventId}")
    @Operation(
            summary = "Update event",
            description = "Updates an existing event"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Event updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or event ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event or category not found"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Event dates violate business rules"
            )
    })
    public ResponseEntity<EventResponse> update(
            @PathVariable UUID eventId,
            @Valid @RequestBody UpdateEventRequest request
    ) {

        EventResponse response =
                eventService.updateEvent(eventId, request);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{eventId}")
    @Operation(
            summary = "Get event by ID",
            description = "Retrieves a specific event by its ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Event retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid event ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found"
            )
    })
    public ResponseEntity<EventResponse> getEventById(
            @PathVariable UUID eventId
    ) {

        return ResponseEntity.ok(
                eventService.getEventById(eventId)
        );
    }


    @GetMapping
    @Operation(
            summary = "Get all events",
            description = "Retrieves all events with pagination and sorting"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Events retrieved successfully"
    )
    public ResponseEntity<Page<EventResponse>> getAllEvents(
            @ParameterObject Pageable pageable
    ) {

        return ResponseEntity.ok(
                eventService.getAllEvents(pageable)
        );
    }


    @GetMapping("/status/{status}")
    @Operation(
            summary = "Get events by status",
            description = "Retrieves events filtered by their current status"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Events retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid event status"
            )
    })
    public ResponseEntity<Page<EventResponse>> getEventsByStatus(
            @PathVariable EventStatus status,
            @ParameterObject Pageable pageable
    ) {

        return ResponseEntity.ok(
                eventService.getEventByStatus(status, pageable)
        );
    }


    @GetMapping("/organizer/{organizerId}")
    @Operation(
            summary = "Get events by organizer",
            description = "Retrieves events created by a specific organizer"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Events retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid organizer ID"
            )
    })
    public ResponseEntity<Page<EventResponse>> getEventsByOrganizer(
            @PathVariable UUID organizerId,
            @ParameterObject Pageable pageable
    ) {

        return ResponseEntity.ok(
                eventService.getEventByOrganizer(
                        organizerId,
                        pageable
                )
        );
    }


    @GetMapping("/category/{categoryId}")
    @Operation(
            summary = "Get events by category",
            description = "Retrieves events belonging to a specific category"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Events retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid category ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            )
    })
    public ResponseEntity<Page<EventResponse>> getEventsByCategory(
            @PathVariable UUID categoryId,
            @ParameterObject Pageable pageable
    ) {

        return ResponseEntity.ok(
                eventService.getEventByCategory(
                        categoryId,
                        pageable
                )
        );
    }


    @GetMapping("/search")
    @Operation(
            summary = "Search events",
            description = "Searches events by title"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Search completed successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Missing or invalid search parameters"
            )
    })
    public ResponseEntity<Page<EventResponse>> searchEvents(
            @RequestParam String title,
            @ParameterObject Pageable pageable
    ) {

        return ResponseEntity.ok(
                eventService.searchEvents(
                        title,
                        pageable
                )
        );
    }


    @DeleteMapping("/{eventId}")
    @Operation(
            summary = "Delete event",
            description = "Deletes an existing event"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Event deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid event ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Event cannot be deleted because ticket types are associated with it"
            )
    })
    public ResponseEntity<Void> deleteEvent(
            @PathVariable UUID eventId
    ) {

        eventService.deleteEvent(eventId);

        return ResponseEntity
                .noContent()
                .build();
    }
}