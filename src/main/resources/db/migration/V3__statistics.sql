create table statistic(
    id boolean primary key default true constraint statistic_singleton check (id = true),
    statistics_epoch timestamp with time zone not null default now(),
    startups smallint not null default 0 check (startups >= 0),
    spoiler_messages integer not null default 0 check (spoiler_messages >= 0),
    spoiler_files integer not null default 0 check (spoiler_messages >= 0),
    xfer_kb bigint not null default 0 check (xfer_kb >= 0),
    info_provided integer not null default 0 check (info_provided >= 0),
    messages_seen bigint not null default 0 check (messages_seen >= 0),
    reactions_seen bigint not null default 0 check (reactions_seen >= 0),
    guild_joins integer not null default 0 check (guild_joins >= 0),
    guild_leaves integer not null default 0 check (guild_leaves >= 0)
);

insert into statistic default values;
