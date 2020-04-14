package site.markhenrick.mobilespoilers.discord.listeners;

import site.markhenrick.mobilespoilers.discord.deletion.Deleter;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionDeletionListener extends ListenerAdapter {
	private final String deletionEmoji;
	private final Deleter deleter;

	public ReactionDeletionListener(String deletionEmoji, Deleter deleter) {
		this.deletionEmoji = deletionEmoji;
		this.deleter = deleter;
	}

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {
		var author = event.getUser();
		var emote = event.getReactionEmote();
		if (author != null && !author.isBot() && emote.isEmoji() && emote.getEmoji().contains(deletionEmoji)) {
			deleter.tryDeleteMessage(author.getId(), event.getMessageId()).queue();
		}
	}
}
