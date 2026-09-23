package com.eventhub.event.controller;

import com.eventhub.event.dto.request.CreateTicketTypeRequest;
import com.eventhub.event.dto.request.UpdateTicketTypeRequest;
import com.eventhub.event.dto.response.TicketTypeResponse;
import com.eventhub.event.services.TicketTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events/{eventId}/ticket-types")
@RequiredArgsConstructor
@Tag(
        name = "Ticket Types",
        description = "Ticket type management APIs"
)
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;


    @PostMapping
    @Operation(
            summary = "Create ticket type",
            description = "Creates a new ticket type for a specific event"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Ticket type created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or event ID"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not authorized to modify ticket types for this event"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Ticket type with the same name already exists for this event"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Ticket types can only be modified while the event is in DRAFT state"
            )
    })
    public ResponseEntity<TicketTypeResponse> createTicketType(
            @PathVariable UUID eventId,
            @Valid @RequestBody CreateTicketTypeRequest request,

            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt,

            @Parameter(hidden = true)
            Authentication authentication
    ) {

        UUID currentUserId =
                UUID.fromString(jwt.getSubject());

        boolean admin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        TicketTypeResponse response =
                ticketTypeService.createTicketType(
                        eventId,
                        request,
                        currentUserId,
                        admin
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @GetMapping
    @Operation(
            summary = "Get all ticket types",
            description = "Retrieves all ticket types belonging to a specific event"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ticket types retrieved successfully"
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
    public ResponseEntity<List<TicketTypeResponse>> getAllTicketTypes(
            @PathVariable UUID eventId
    ) {

        List<TicketTypeResponse> response =
                ticketTypeService.getTicketTypesByEvent(eventId);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{ticketTypeId}")
    @Operation(
            summary = "Get ticket type by ID",
            description = "Retrieves a specific ticket type belonging to a specific event"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ticket type retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid event ID or ticket type ID"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ticket type not found for this event"
            )
    })
    public ResponseEntity<TicketTypeResponse> getTicketType(
            @PathVariable UUID eventId,
            @PathVariable UUID ticketTypeId
    ) {

        TicketTypeResponse response =
                ticketTypeService.getTicketTypeById(
                        eventId,
                        ticketTypeId
                );

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{ticketTypeId}")
    @Operation(
            summary = "Update ticket type",
            description = "Updates an existing ticket type belonging to a specific event"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Ticket type updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data, event ID or ticket type ID"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not authorized to modify ticket types for this event"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ticket type not found for this event"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Another ticket type with the same name already exists for this event"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Ticket types can only be modified while the event is in DRAFT state"
            )
    })
    public ResponseEntity<TicketTypeResponse> updateTicketType(
            @PathVariable UUID eventId,
            @PathVariable UUID ticketTypeId,
            @Valid @RequestBody UpdateTicketTypeRequest request,

            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt,

            @Parameter(hidden = true)
            Authentication authentication
    ) {

        UUID currentUserId =
                UUID.fromString(jwt.getSubject());

        boolean admin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        TicketTypeResponse response =
                ticketTypeService.updateTicketType(
                        eventId,
                        ticketTypeId,
                        request,
                        currentUserId,
                        admin
                );

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{ticketTypeId}")
    @Operation(
            summary = "Delete ticket type",
            description = "Deletes an existing ticket type belonging to a specific event"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Ticket type deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid event ID or ticket type ID"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not authorized to modify ticket types for this event"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Ticket type not found for this event"
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Ticket types can only be modified while the event is in DRAFT state"
            )
    })
    public ResponseEntity<Void> deleteTicketType(
            @PathVariable UUID eventId,
            @PathVariable UUID ticketTypeId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt,

            @Parameter(hidden = true)
            Authentication authentication
    ) {

        UUID currentUserId =
                UUID.fromString(jwt.getSubject());

        boolean admin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        ticketTypeService.deleteTicketType(
                eventId,
                ticketTypeId,
                currentUserId,
                admin
        );

        return ResponseEntity
                .noContent()
                .build();
    }
}