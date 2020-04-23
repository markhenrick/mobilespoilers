package site.markhenrick.mobilespoilers.discord.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.requests.RestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import site.markhenrick.mobilespoilers.MobileSpoilersConfig;
import site.markhenrick.mobilespoilers.discord.util.BotException;
import site.markhenrick.mobilespoilers.discord.service.Deleter;
import site.markhenrick.mobilespoilers.discord.service.RestActionFactory;

@Service
public class DeleteCommand extends SelfRegisteringCommand {
	private static final String ERROR_HELP_MESSAGE = "Usage: delete <message ID>";

	private static final Logger LOG = LoggerFactory.getLogger(DeleteCommand.class);

	private final Deleter deleter;
	private final RestActionFactory restActionFactory;

	public DeleteCommand(MobileSpoilersConfig config, Deleter deleter, RestActionFactory restActionFactory) {
		this.name = "delete";
		this.help = String.format("Delete your spoiler by ID (you can also just react to it with %s)", config.getDeletionEmoji());
		this.arguments = "<spoiler message ID>";
		this.guildOnly = false;
		this.deleter = deleter;
		this.restActionFactory = restActionFactory;
	}

	@Override
	protected void execute(CommandEvent event) {
		assertArgumentLength(event)
			.flatMap(this::parseLong)
			.flatMap(messageId -> deleter.tryDeleteMessage(event.getAuthor().getIdLong(), messageId))
			.queue(result -> {}, error -> {
				if (error instanceof BotException) {
					event.reply(error.getMessage());
				} else {
					LOG.error("RestAction from deleter threw", error);
					event.reply("Sorry, an unknown error occurred when deleting that spoiler");
				}
			});
	}

	private RestAction<String> assertArgumentLength(CommandEvent event) {
		@SuppressWarnings("ProblematicWhitespace") var args = event.getArgs().split(" ");
		return restActionFactory.fromBoolean(args.length == 1, args[0], ERROR_HELP_MESSAGE);
	}

	private RestAction<Long> parseLong(String longString) {
		try {
			return restActionFactory.resolve(Long.parseLong(longString));
		} catch (NumberFormatException e) {
			return restActionFactory.error(ERROR_HELP_MESSAGE);
		}
	}
}
