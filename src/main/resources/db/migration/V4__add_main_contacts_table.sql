CREATE TABLE "user"."main_contacts"
(
    id           UUID         NOT NULL UNIQUE,
    first_name   VARCHAR(255) NOT NULL,
    last_name    VARCHAR(255) NOT NULL,
    middle_name  VARCHAR(255),
    role         VARCHAR(255) NOT NULL,
    email        VARCHAR(255) NOT NULL UNIQUE,
    tg_link      VARCHAR(1000),
    vk_link      VARCHAR(1000),
    CONSTRAINT pk_main_contacts PRIMARY KEY (id)
);

alter table "user"."users"
    drop column tg_link;

alter table "user"."users"
    drop column vk_link;