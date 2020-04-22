create table spoiler(
    message_id bigint primary key,
    channel_id bigint not null,
    user_id bigint not null
);
