package com.eventhub.booking.reservation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservedTicketCounterServiceImpl
        implements ReservedTicketCounterService {

    private final StringRedisTemplate stringRedisTemplate;

    private static final DefaultRedisScript<Long> RELEASE_SCRIPT =
            new DefaultRedisScript<>(
                    """
                    local current = tonumber(redis.call('GET', KEYS[1]) or '0')
                    local quantity = tonumber(ARGV[1])
    
                    if current <= quantity then
                        redis.call('DEL', KEYS[1])
                        return 0
                    else
                        return redis.call('DECRBY', KEYS[1], quantity)
                    end
                    """,
                    Long.class
            );

    private static final DefaultRedisScript<Long> RESERVE_SCRIPT =
            new DefaultRedisScript<>(
                    """
                    local current = tonumber(redis.call('GET', KEYS[1]) or '0')
                    local requested = tonumber(ARGV[1])
                    local capacity = tonumber(ARGV[2])
    
                    if current + requested <= capacity then
                        redis.call('INCRBY', KEYS[1], requested)
                        return 1
                    else
                        return 0
                    end
                    """,
                    Long.class
            );
    @Override
    public long getReservedQuantity(UUID ticketTypeId) {

        String key = ReservationKeys.bookingReserved(ticketTypeId);

        String value = stringRedisTemplate.opsForValue().get(key);

        if (value == null) {
            return 0L;
        }

        return Long.parseLong(value);
    }

    @Override
    public boolean reserveTickets(UUID ticketTypeId, int quantity, int capacity) {

        String key = ReservationKeys.bookingReserved(ticketTypeId);

        Long result = stringRedisTemplate.execute(
                RESERVE_SCRIPT,
                List.of(key),
                String.valueOf(quantity),
                String.valueOf(capacity)
        );

        return result != null && result == 1L;
    }

    @Override
    public void releaseTickets(UUID ticketTypeId, int quantity) {
        String key = ReservationKeys.bookingReserved(ticketTypeId);

        stringRedisTemplate.execute(
                RELEASE_SCRIPT,
                List.of(key),
                String.valueOf(quantity)
        );
    }


}