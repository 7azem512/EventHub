package com.eventhub.booking.controller;

import com.eventhub.booking.dtos.request.CreateBookingRequest;
import com.eventhub.booking.dtos.response.BookingResponse;
import com.eventhub.booking.exception.BookingAccessDeniedException;
import com.eventhub.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
@Tag(
        name = "Bookings",
        description = "APIs for creating, viewing, confirming and cancelling event bookings"
)
@SecurityRequirement(name = "keycloak")
public class BookingController {

    private final BookingService bookingService;


    @Operation(
            summary = "Create a booking",
            description = """
                    Creates a new PENDING booking for the authenticated user.
                    Ticket capacity is reserved temporarily in Redis while the booking is pending.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Booking created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid booking request"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event or ticket type not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Not enough ticket capacity available"
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Event service is currently unavailable"
            )
    })
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request,

            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt
    ) {

        UUID userId = UUID.fromString(jwt.getSubject());

        BookingResponse bookingResponse =
                bookingService.createBooking(request, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingResponse);
    }


    @Operation(
            summary = "Get my bookings",
            description = "Returns all bookings belonging to the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Bookings retrieved successfully"
            )
    })
    @GetMapping("/me")
    public ResponseEntity<List<BookingResponse>> getMyBookings(

            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt
    ) {

        UUID userId = UUID.fromString(jwt.getSubject());

        List<BookingResponse> bookingResponses =
                bookingService.getMyBookings(userId);

        return ResponseEntity.ok(bookingResponses);
    }


    @Operation(
            summary = "Get booking by ID",
            description = """
                    Returns a booking by its ID.
                    The booking can be viewed by its owner or an administrator.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Booking retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not allowed to view this booking"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Booking not found"
            )
    })
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(

            @Parameter(description = "Booking ID", required = true)
            @PathVariable UUID bookingId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt,

            @Parameter(hidden = true)
            Authentication authentication
    ) {

        UUID userId = UUID.fromString(jwt.getSubject());

        boolean admin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        BookingResponse bookingResponse =
                bookingService.getBookingById(
                        bookingId,
                        userId,
                        admin
                );

        return ResponseEntity.ok(bookingResponse);
    }


    @Operation(
            summary = "Get bookings for an event",
            description = """
                    Returns all bookings belonging to an event.
                    Only the event organizer or an administrator can access this endpoint.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Event bookings retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Only the event organizer or an administrator can view these bookings"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Event not found"
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Event service is currently unavailable"
            )
    })
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByEventId(

            @Parameter(description = "Event ID", required = true)
            @PathVariable UUID eventId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt,

            @Parameter(hidden = true)
            Authentication authentication
    ) {

        UUID userId = UUID.fromString(jwt.getSubject());

        boolean admin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        boolean organizer = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ORGANIZER")
                );

        if (!admin && !organizer) {
            throw new BookingAccessDeniedException(
                    "Only organizers or admins can view event bookings"
            );
        }

        List<BookingResponse> bookingResponses =
                bookingService.getBookingsByEventId(
                        eventId,
                        userId,
                        admin
                );

        return ResponseEntity.ok(bookingResponses);
    }


    @Operation(
            summary = "Cancel a booking",
            description = """
                    Cancels a PENDING booking.
                    When cancellation succeeds, the temporary Redis reservation is removed
                    and the reserved ticket capacity is released.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Booking cancelled successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not allowed to cancel this booking"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Booking not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Booking is not in PENDING state"
            )
    })
    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(

            @Parameter(description = "Booking ID", required = true)
            @PathVariable UUID bookingId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal Jwt jwt,

            @Parameter(hidden = true)
            Authentication authentication
    ) {

        UUID userId = UUID.fromString(jwt.getSubject());

        boolean admin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        BookingResponse bookingResponse =
                bookingService.cancelBooking(
                        bookingId,
                        userId,
                        admin
                );

        return ResponseEntity.ok(bookingResponse);
    }


    @Operation(
            summary = "Confirm a booking",
            description = """
                    Confirms a PENDING booking.
                    Once confirmed, the temporary Redis reservation is removed,
                    but the ticket capacity remains allocated to the booking.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Booking confirmed successfully"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User is not allowed to confirm this booking"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Booking not found"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Booking is not in PENDING state"
            )
    })
    @PatchMapping("/{bookingId}/confirm")
    public ResponseEntity<BookingResponse> confirmBooking(@Parameter(description = "Booking ID", required = true) @PathVariable UUID bookingId, @Parameter(hidden = true) @AuthenticationPrincipal Jwt jwt, @Parameter(hidden = true) Authentication authentication) {

        UUID userId = UUID.fromString(jwt.getSubject());

        boolean admin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        BookingResponse bookingResponse = bookingService.confirmBooking(bookingId, userId, admin);

        return ResponseEntity.ok(bookingResponse);
    }
}