package com.eventhub.booking.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Setter
@Getter
@ConfigurationProperties(prefix = "booking.reservation")
@Component
public class BookingReservationProperties {
    private Duration ttl;
}
