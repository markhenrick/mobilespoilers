-- Change of plans. The StatsService will now insert the row when it needs it, to keep the epoch somewhat accurate

-- Existing deployments - StatsService has never been enabled iff startups = 0, so drop it
delete from statistic where startups = 0;
