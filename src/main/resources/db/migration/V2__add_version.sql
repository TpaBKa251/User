alter table "user"."roles"
    add column version bigint not null default 0;

alter table "user"."sessions"
    add column version bigint not null default 0;