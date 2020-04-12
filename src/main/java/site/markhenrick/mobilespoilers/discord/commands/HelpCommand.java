package site.markhenrick.mobilespoilers.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.MessageBuilder;

import java.util.function.Consumer;

public class HelpCommand implements Consumer<CommandEvent> {
	// This code has plenty of areas for optimisation, but it's unlikely to need it

	private final boolean showAdminInfo;

	public HelpCommand(final boolean showAdminInfo) {
		this.showAdminInfo = showAdminInfo;
	}

	@Override
	public void accept(final CommandEvent event) {
		final var client = event.getClient();
		final var text = new StringBuilder("**Mobile Spoilers** commands:\n\n");
		client.getCommands().stream()
			.filter(command -> !command.isHidden() && !command.isOwnerCommand())
			.forEach(command -> text.append(describeCommand(event, command)));
		if (event.isOwner()) {
			text.append("\nOwner commands:\n\n");
			client.getCommands().stream()
				.filter(command -> !command.isHidden() && command.isOwnerCommand())
				.forEach(command -> text.append(describeCommand(event, command)));
		}
		final var message = new MessageBuilder()
			.setContent(text.toString())
			.setEmbed(AboutCommand.getEmbed(event, showAdminInfo))
			.build();
		event.reply(message);
	}

	private static String describeCommand(final CommandEvent event,  final Command command) {
		final var client = event.getClient();
		final var args = command.getArguments() != null ? String.format(" %s", command.getArguments()) : "";
		final var help = command.getHelp() != null ? String.format(" - %s", command.getHelp()) : "";
		return String.format("`%s%s%s`%s\n", client.getPrefix(), command.getName(), args, help);
	}
}
