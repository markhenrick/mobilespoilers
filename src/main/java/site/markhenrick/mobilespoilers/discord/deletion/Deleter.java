package site.markhenrick.mobilespoilers.discord.deletion;

import net.dv8tion.jda.api.requests.RestAction;

public interface Deleter {
	RestAction<DeletionResult> tryDeleteMessage(String requestingUserId, String messageId);

	enum DeletionResult {
		SUCCESS, SPOILER_NOT_FOUND, UNAUTHORISED, CHANNEL_NOT_FOUND, UNKNOWN_ERROR
	}
}
