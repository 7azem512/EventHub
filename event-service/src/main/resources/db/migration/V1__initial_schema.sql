CREATE TABLE categories (
                            id UUID NOT NULL,
                            description VARCHAR(500),
                            name VARCHAR(100) NOT NULL,
                            slug VARCHAR(120) NOT NULL,

                            CONSTRAINT pk_categories PRIMARY KEY (id),
                            CONSTRAINT uk_categories_name UNIQUE (name),
                            CONSTRAINT uk_categories_slug UNIQUE (slug)
);


CREATE TABLE events (
                        id UUID NOT NULL,
                        booking_end_date TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                        booking_start_date TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                        created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                        description TEXT,
                        end_date TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                        location VARCHAR(255),
                        organizer_id UUID NOT NULL,
                        start_date TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                        status VARCHAR(50) NOT NULL,
                        title VARCHAR(200) NOT NULL,
                        updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                        category_id UUID NOT NULL,

                        CONSTRAINT pk_events PRIMARY KEY (id),

                        CONSTRAINT chk_events_status CHECK (
                            status IN (
                                       'DRAFT',
                                       'PENDING_APPROVAL',
                                       'PUBLISHED',
                                       'REJECTED',
                                       'CANCELLED',
                                       'COMPLETED'
                                )
                            ),

                        CONSTRAINT fk_events_category
                            FOREIGN KEY (category_id)
                                REFERENCES categories(id)
);


CREATE TABLE ticket_types (
                              id UUID NOT NULL,
                              capacity INTEGER NOT NULL,
                              created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                              name VARCHAR(100) NOT NULL,
                              price NUMERIC(10,2) NOT NULL,
                              updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                              event_id UUID NOT NULL,

                              CONSTRAINT pk_ticket_types PRIMARY KEY (id),

                              CONSTRAINT chk_ticket_types_capacity
                                  CHECK (capacity >= 1),

                              CONSTRAINT fk_ticket_types_event
                                  FOREIGN KEY (event_id)
                                      REFERENCES events(id)
);