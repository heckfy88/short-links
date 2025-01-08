create extension if not exists "uuid-ossp";

create table link(
    id uuid not null,
    owner_uid uuid not null,
    url varchar(255) not null,
    short_url varchar(255) unique not null,
    created_at timestamp default now(),
    expiration_time time not null,
    counter int not null default 0,
    lim int not null,
    is_active boolean default true,

    primary key (id),
    unique (owner_uid, url),
    unique (owner_uid, short_url),
    check (counter <= lim)
)