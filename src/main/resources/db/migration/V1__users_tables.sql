CREATE SCHEMA IF NOT EXISTS "user";

CREATE TABLE "user"."users"
(
    id           UUID         NOT NULL UNIQUE,
    first_name   VARCHAR(255) NOT NULL,
    last_name    VARCHAR(255) NOT NULL,
    middle_name  VARCHAR(255),
    email        VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(255) UNIQUE,
    password     VARCHAR(255) NOT NULL,
    room_number  VARCHAR(255) NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);

CREATE TABLE "user"."sessions"
(
    id              UUID                        NOT NULL UNIQUE,
    "user"          UUID                        NOT NULL,
    create_date     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    expiration_date TIMESTAMP WITHOUT TIME ZONE,
    refresh_token   VARCHAR(1024) UNIQUE,
    CONSTRAINT pk_session PRIMARY KEY (id),
    CONSTRAINT fk_user FOREIGN KEY ("user") REFERENCES "user"."users" (id) ON DELETE CASCADE
);

CREATE TABLE "user"."roles"
(
    id     UUID         NOT NULL UNIQUE,
    "user" UUID         NOT NULL,
    role   VARCHAR(255) NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uq_roles_role_and_user UNIQUE ("user", role),
    CONSTRAINT fk_roles FOREIGN KEY ("user") REFERENCES "user"."users" (id) ON DELETE CASCADE
);
