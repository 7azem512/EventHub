CREATE TABLE user_profiles (
                               user_id UUID NOT NULL,
                               avatar_media_id UUID,
                               created_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
                               user_email VARCHAR(255) NOT NULL,
                               first_name VARCHAR(100) NOT NULL,
                               keycloak_user_id VARCHAR(100) NOT NULL,
                               last_name VARCHAR(100) NOT NULL,
                               phone_number VARCHAR(30),
                               updated_at TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,

                               CONSTRAINT pk_user_profiles PRIMARY KEY (user_id),
                               CONSTRAINT uk_user_profiles_keycloak_user_id UNIQUE (keycloak_user_id),
                               CONSTRAINT uk_user_profiles_user_email UNIQUE (user_email)
);