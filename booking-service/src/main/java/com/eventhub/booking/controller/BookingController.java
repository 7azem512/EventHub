package com.eventhub.booking.controller;

import com.eventhub.booking.dtos.request.CreateBookingRequest;
import com.eventhub.booking.dtos.response.BookingResponse;
import com.eventhub.booking.exception.BookingAccessDeniedException;
import com.eventhub.booking.service.BookingService;
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
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request, @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        BookingResponse bookingResponse = bookingService.createBooking(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<List<BookingResponse>> getMyBookings(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<BookingResponse> bookingResponses = bookingService.getMyBookings(userId);
        return ResponseEntity.ok(bookingResponses);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable UUID bookingId, @AuthenticationPrincipal Jwt jwt, Authentication authentication) {
        UUID userId = UUID.fromString(jwt.getSubject());
        boolean admin=authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );
        BookingResponse bookingResponse = bookingService.getBookingById(bookingId, userId ,admin);
        return ResponseEntity.ok(bookingResponse);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByEventId(@PathVariable UUID eventId, @AuthenticationPrincipal Jwt jwt, Authentication authentication) {
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
            throw new BookingAccessDeniedException("Only organizers or admins can view event bookings");
        }

        List<BookingResponse> bookingResponses = bookingService.getBookingsByEventId(eventId, userId, admin);

        return ResponseEntity.ok(bookingResponses);
    }

    @PatchMapping("/{bookingId}/cancel")
    public ResponseEntity <BookingResponse> cancelBooking(@PathVariable UUID bookingId, @AuthenticationPrincipal Jwt jwt, Authentication authentication) {
        UUID userId = UUID.fromString(jwt.getSubject());
        boolean admin = authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );

        BookingResponse bookingResponse = bookingService.cancelBooking(bookingId, userId, admin);

        return ResponseEntity.ok(bookingResponse);
    }
}
