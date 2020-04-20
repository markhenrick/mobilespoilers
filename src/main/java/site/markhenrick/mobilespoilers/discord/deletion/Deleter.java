package site.markhenrick.mobilespoilers.discord.deletion;

import net.dv8tion.jda.api.requests.RestAction;
import site.markhenrick.mobilespoilers.util.Unit;

public interface Deleter {
	RestAction<Unit> tryDeleteMessage(String requestingUserId, String messageId);
}
