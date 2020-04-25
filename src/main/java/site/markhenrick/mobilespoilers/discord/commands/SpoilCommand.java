package site.markhenrick.mobilespoilers.discord.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.utils.AttachmentOption;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import site.markhenrick.mobilespoilers.MobileSpoilersConfig;
import site.markhenrick.mobilespoilers.dal.Spoiler;
import site.markhenrick.mobilespoilers.dal.SpoilerRepository;
import site.markhenrick.mobilespoilers.services.StatisticsService;
import site.markhenrick.mobilespoilers.util.AsyncBarrier;

import java.io.IOException;
import java.util.Collection;

import static net.dv8tion.jda.api.Permission.MESSAGE_MANAGE;

@Service
public class SpoilCommand extends SelfRegisteringCommand {
	private static final Logger LOG = LoggerFactory.getLogger(SpoilCommand.class);

	private final MobileSpoilersConfig config;
	private final SpoilerRepository repo;
	private final StatisticsService stats;

	public SpoilCommand(MobileSpoilersConfig config, SpoilerRepository repo, StatisticsService stats) {
		this.name = "spoil";
		this.aliases = new String[] { "spoiler" };
		this.arguments = "[optional message - will *not* be hidden]";
		this.guildOnly = false;
		this.config = config;
		this.repo = repo;
		this.stats = stats;
	}

	@Override
	protected void execute(CommandEvent event) {
		var message = event.getMessage();
		var attachments = message.getAttachments();

		if (attachments.isEmpty()) {
			reportError(event, "Please attach some images to be spoiled");
			return;
		}

		var barrier = new AsyncBarrier<DownloadResult, IOException>(attachments.size(), results -> {
			LOG.trace("All attachments downloaded");
			deleteMessage(event);
			sendSpoilerMessage(event, results);
		}, e -> reportError(event, "Something went wrong when downloading your attachment"));

		for (var attachment : attachments) {
			var request = new Request.Builder().url(attachment.getUrl()).build();
			LOG.trace("Downloading attachment {}", request);

			event.getJDA().getHttpClient().newCall(request).enqueue(new Callback() {
				@Override
				public void onResponse(Call call, Response response) throws IOException {
					try (var body = response.body()) {
						LOG.trace("Finished download {}", request);
						assert body != null;
						var bytes = body.bytes();
						barrier.addResult(new DownloadResult(attachment.getFileName(), bytes));
						stats.recordXfer(bytes.length);
					}
				}

				@Override
				public void onFailure(Call call, IOException e) {
					LOG.error("Error in download", e);
					barrier.error(e);
				}
			});
		}
	}

	private static void reportError(CommandEvent event, String response) {
		event.reply(response);
		event.getMessage().delete().queue();
	}

	private static void deleteMessage(CommandEvent event) {
		var message = event.getMessage();
		LOG.debug("Attempting to delete message {}", message.getId());
		if (!(event.getChannel() instanceof GuildChannel)) {
			LOG.debug("Not trying to delete message from a {}", event.getChannel().getClass());
			return;
		}
		var channel = (GuildChannel) event.getChannel();
		if (event.getSelfMember().hasPermission(channel, MESSAGE_MANAGE)) {
			message.delete().queue(success -> {}, error -> {
				LOG.error("Error deleting message", error);
				event.reply("I couldn't delete your original message. You'll have to do it yourself");
			});
		} else {
			event.reply("I don't have permission to delete your original message. You'll have to do it yourself");
		}
	}

	private void sendSpoilerMessage(CommandEvent event, Collection<? extends DownloadResult> results) {
		var args = event.getArgs();
		var channel = event.getChannel();
		var author = event.getAuthor();
		var spoilerMessage = new MessageBuilder();
		spoilerMessage.setContent(String.format("Spoiler from %s%s", author, !args.isEmpty() ? String.format("\n> %s", args) : ""));
		var messageAction = spoilerMessage
			.sendTo(event.getChannel());
		for (var result : results) {
			messageAction = messageAction.addFile(result.data, result.originalFilename, AttachmentOption.SPOILER);
		}
		messageAction.queue(sentMessage -> {
			sentMessage.addReaction(config.getDeletionEmoji()).queue();
			var spoiler = new Spoiler();
			spoiler.setMessageId(sentMessage.getIdLong());
			spoiler.setChannelId(channel.getIdLong());
			spoiler.setUserId(author.getIdLong());
			LOG.debug("Recording spoiler {}", spoiler);
			repo.save(spoiler);
			stats.recordSpoiler(results.size());
		});
	}

	private static class DownloadResult {
		final String originalFilename;
		final byte[] data;

		@SuppressWarnings("MethodCanBeVariableArityMethod")
		DownloadResult(String originalFilename, byte[] data) {
			this.originalFilename = originalFilename;
			this.data = data;
		}
	}
}
