alter table "user"."roles"
    add column version bigint not null default 0;