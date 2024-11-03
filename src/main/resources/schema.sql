create table if not exists posts
(
    id                  identity,
    message             varchar(1000)               not null,
    created_by          varchar(255)                not null,
    last_modified_by    varchar(255)                not null,
    created_date        timestamp(6) with time zone not null,
    last_modified_date  timestamp(6) with time zone not null,
    primary key (id)
);