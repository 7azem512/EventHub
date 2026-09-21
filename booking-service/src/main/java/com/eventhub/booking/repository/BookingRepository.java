package com.eventhub.booking.repository;

import com.eventhub.booking.entity.Booking;
import com.eventhub.booking.enums.BookingStatus;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
    List<Booking> findAllByEventIdOrderByCreatedAtDesc(UUID eventId);
    Optional<Booking> findByIdAndUserId(UUID id, UUID userId);


    @Modifying
    @Query("""
        update Booking b
        set b.status = :newStatus,
            b.updatedAt = CURRENT_TIMESTAMP
        where b.id = :bookingId
          and b.status = :expectedStatus
        """)
    int updateStatusIfCurrent(
            @Param("bookingId") UUID bookingId,
            @Param("expectedStatus") BookingStatus expectedStatus,
            @Param("newStatus") BookingStatus newStatus
    );
}
