alter table "user"."users"
    add column tg_link varchar(1000);

alter table "user"."users"
    add column vk_link varchar(1000);

alter table "user"."users"
    add column version bigint not null default 0;