package com.eventhub.booking.config;

import com.eventhub.booking.reservation.BookingReservation;
import com.eventhub.booking.reservation.ReservationExpirationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, BookingReservation> bookingReservationRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, BookingReservation> template =
                new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());

        JacksonJsonRedisSerializer<BookingReservation> valueSerializer =
                new JacksonJsonRedisSerializer<>(BookingReservation.class);

        template.setValueSerializer(valueSerializer);

        return template;
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            ReservationExpirationListener expirationListener) {

        RedisMessageListenerContainer container =
                new RedisMessageListenerContainer();

        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(
                expirationListener,
                new PatternTopic("__keyevent@*__:expired")
        );

        return container;
    }
}