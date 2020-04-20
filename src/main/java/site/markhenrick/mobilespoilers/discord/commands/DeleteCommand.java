package site.markhenrick.mobilespoilers.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import site.markhenrick.mobilespoilers.discord.deletion.Deleter;
import site.markhenrick.mobilespoilers.discord.deletion.DeletionException;

@CommandInfo(
	name = "Delete",
	description = "Delete your spoiler by ID"
)
@Author("Mark Henrick")
public class DeleteCommand extends Command {
	private static final Logger LOG = LoggerFactory.getLogger(DeleteCommand.class);

	private final Deleter deleter;

	public DeleteCommand(String reaction, Deleter deleter) {
		this.name = "delete";
		this.help = String.format("Delete your spoiler by ID (you can also just react to it with %s)", reaction);
		this.arguments = "<spoiler message ID>";
		this.guildOnly = false;
		this.deleter = deleter;
	}

	@Override
	protected void execute(CommandEvent event) {
		var args = event.getArgs();
		if (args.isEmpty()) {
			event.reply("Please provide the ID of the spoiler that you want to delete");
			return;
		}
		deleter.tryDeleteMessage(event.getAuthor().getId(), args)
			.queue(result -> {}, error -> {
				if (error instanceof DeletionException) {
					event.reply(error.getMessage());
				} else {
					LOG.error("RestAction from deleter threw", error);
					event.reply("Sorry, an unknown error occurred when deleting that spoiler");
				}
			});
	}
}
