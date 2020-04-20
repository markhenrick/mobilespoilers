package site.markhenrick.mobilespoilers.dal;

import site.markhenrick.mobilespoilers.dal.jooqgenerated.tables.records.Spoiler;

public interface SpoilerRepository {
	void recordSpoiler(String messageId, String channelId, String userId);

	Spoiler getSpoiler(String messageId); // TODO use non-jooq DTO

	void deleteSpoiler(String messageId);
}
