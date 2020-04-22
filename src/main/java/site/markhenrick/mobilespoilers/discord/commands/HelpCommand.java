package site.markhenrick.mobilespoilers.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.MessageBuilder;
import org.springframework.stereotype.Service;
import site.markhenrick.mobilespoilers.discord.service.BotInfoService;

import java.util.function.Consumer;

@Service
public class HelpCommand implements Consumer<CommandEvent> {
	// This code has plenty of areas for optimisation, but it's unlikely to need it

	private final BotInfoService botInfoService;

	public HelpCommand(BotInfoService botInfoService) {
		this.botInfoService = botInfoService;
	}

	@Override
	public void accept(CommandEvent event) {
		var client = event.getClient();
		var text = new StringBuilder("**Mobile Spoilers** commands:\n\n");
		client.getCommands().stream()
			.filter(command -> !command.isHidden() && !command.isOwnerCommand())
			.forEach(command -> text.append(describeCommand(event, command)));
		if (event.isOwner()) {
			text.append("\nOwner commands:\n\n");
			client.getCommands().stream()
				.filter(command -> !command.isHidden() && command.isOwnerCommand())
				.forEach(command -> text.append(describeCommand(event, command)));
		}
		var message = new MessageBuilder()
			.setContent(text.toString())
			.setEmbed(botInfoService.getAboutEmbed())
			.build();
		event.reply(message);
	}

	private static String describeCommand(CommandEvent event, Command command) {
		var client = event.getClient();
		var args = command.getArguments() != null ? String.format(" %s", command.getArguments()) : "";
		var help = command.getHelp() != null ? String.format(" - %s", command.getHelp()) : "";
		return String.format("`%s%s%s`%s\n", client.getPrefix(), command.getName(), args, help);
	}
}
