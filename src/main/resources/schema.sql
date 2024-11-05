create table if not exists posts
(
    id                  identity,
    title               varchar(200)               not null,
    body                varchar(1000),
    created_by          varchar(255)                not null,
    last_modified_by    varchar(255)                not null,
    created_date        timestamp(6) with time zone not null,
    last_modified_date  timestamp(6) with time zone not null,
    deleted_date        timestamp(6) with time zone,
    primary key (id)
);