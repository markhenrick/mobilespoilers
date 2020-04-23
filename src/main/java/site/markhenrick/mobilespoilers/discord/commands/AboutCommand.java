package site.markhenrick.mobilespoilers.discord.commands;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.Permission;
import org.springframework.stereotype.Service;
import site.markhenrick.mobilespoilers.discord.service.BotInfoService;

import static net.dv8tion.jda.api.Permission.MESSAGE_EMBED_LINKS;

@Service
public class AboutCommand extends SelfRegisteringCommand {
	private final BotInfoService botInfoService;

	public AboutCommand(BotInfoService botInfoService) {
		this.botInfoService = botInfoService;
		this.name = "about";
		this.aliases = new String[] { "info", "invite", "ping" };
		this.help = "show info about the bot and its admin, and get an invite link to add it to your server";
		this.guildOnly = false;
		this.hidden = true;
		this.botPermissions = new Permission[]{ MESSAGE_EMBED_LINKS };
	}

	@Override
	protected void execute(CommandEvent event) {
		event.reply(botInfoService.getAboutEmbed());
	}
}
