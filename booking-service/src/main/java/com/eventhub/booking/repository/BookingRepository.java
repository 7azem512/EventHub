package com.eventhub.booking.repository;

import com.eventhub.booking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
    List<Booking> findAllByEventIdOrderByCreatedAtDesc(UUID eventId);
    Optional<Booking> findByIdAndUserId(UUID id, UUID userId);
}
