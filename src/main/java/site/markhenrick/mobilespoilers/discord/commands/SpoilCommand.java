package site.markhenrick.mobilespoilers.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.utils.AttachmentOption;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.markhenrick.mobilespoilers.dal.SpoilerRepository;
import site.markhenrick.mobilespoilers.util.Barrier;

import java.io.IOException;

import static net.dv8tion.jda.api.Permission.MESSAGE_MANAGE;

@CommandInfo(
	name = { "Spoil", "Spoiler" },
	description = "Create a spoiler"
)
@Author("Mark Henrick")
public class SpoilCommand extends Command {
	private static final Logger LOG = LoggerFactory.getLogger(SpoilCommand.class);

	private final SpoilerRepository repo;
	private final String reaction;

	public SpoilCommand(final SpoilerRepository repo, final String reaction) {
		this.name = "spoil";
		this.aliases = new String[] { "spoiler" };
		this.arguments = "[optional message - will *not* be hidden]";
		this.guildOnly = false;
		this.repo = repo;
		this.reaction = reaction;
	}

	@Override
	protected void execute(final CommandEvent event) {
		final var message = event.getMessage();
		final var attachments = message.getAttachments();

		if (attachments.isEmpty()) {
			reportError(event, "Please attach some images to be spoiled");
			return;
		}

		final var barrier = new Barrier<DownloadResult>(attachments.size(), results -> {
			deleteMessage(event);
			sendSpoilerMessage(event, results);
		});
		for (final var attachment : attachments) {
			final var request = new Request.Builder().url(attachment.getUrl()).build();

			event.getJDA().getHttpClient().newCall(request).enqueue(new Callback() {
				@Override
				public void onResponse(final Call call, final Response response) throws IOException {
					try (final var body = response.body()) {
						assert body != null;
						barrier.addResult(new DownloadResult(attachment.getFileName(), body.bytes()));
					}
				}

				@Override
				public void onFailure(final Call call, final IOException e) {
					LOG.error("Error downloading attachment", e);
					reportError(event, "Something went wrong when downloading your attachment");
				}
			});
		}
	}

	private static void reportError(final CommandEvent event, final String response) {
		event.reply(response);
		event.getMessage().delete().queue();
	}

	private static void deleteMessage(final CommandEvent event) {
		if (!(event.getChannel() instanceof GuildChannel)) return;
		final var message = event.getMessage();
		final var channel = (GuildChannel) event.getChannel();
		if (event.getSelfMember().hasPermission(channel, MESSAGE_MANAGE)) {
			message.delete().queue(success -> {}, error -> {
				LOG.error("Error deleting message", error);
				event.reply("I couldn't delete your original message. You'll have to do it yourself");
			});
		} else {
			event.reply("I don't have permission to delete your original message. You'll have to do it yourself");
		}
	}

	private void sendSpoilerMessage(final CommandEvent event, final Iterable<? extends DownloadResult> results) {
		final var args = event.getArgs();
		final var channel = event.getChannel();
		final var author = event.getAuthor();
		final var spoilerMessage = new MessageBuilder();
		spoilerMessage.setContent(String.format("Spoiler from %s%s", author, !args.isEmpty() ? String.format("\n> %s", args) : ""));
		var messageAction = spoilerMessage
			.sendTo(event.getChannel());
		for (final var result : results) {
			messageAction = messageAction.addFile(result.data, result.originalFilename, AttachmentOption.SPOILER);
		}
		messageAction.queue(sentMessage -> {
			sentMessage.addReaction(reaction).queue();
			repo.recordSpoiler(sentMessage.getId(), channel.getId(), author.getId());
		});
	}

	private static class DownloadResult {
		final String originalFilename;
		final byte[] data;

		DownloadResult(final String originalFilename, final byte[] data) {
			this.originalFilename = originalFilename;
			this.data = data;
		}
	}
}
