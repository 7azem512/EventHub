CREATE TABLE booking (
                         id UUID NOT NULL,
                         created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                         event_id UUID NOT NULL,
                         quantity INTEGER NOT NULL,
                         status VARCHAR(255) NOT NULL,
                         ticket_type_id UUID NOT NULL,
                         total_amount NUMERIC(10,2) NOT NULL,
                         unit_price NUMERIC(10,2) NOT NULL,
                         updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                         user_id UUID NOT NULL,

                         CONSTRAINT pk_booking PRIMARY KEY (id),

                         CONSTRAINT chk_booking_quantity
                             CHECK (quantity >= 1),

                         CONSTRAINT chk_booking_status
                             CHECK (
                                 status IN (
                                            'PENDING',
                                            'CONFIRMED',
                                            'CANCELLED',
                                            'EXPIRED'
                                     )
                                 )
);