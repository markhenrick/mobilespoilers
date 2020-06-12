-- Timestamp can be extracted from message ID so dropping this column to renormalise things
-- https://discord.com/developers/docs/reference#snowflakes
alter table spoiler drop column created;
