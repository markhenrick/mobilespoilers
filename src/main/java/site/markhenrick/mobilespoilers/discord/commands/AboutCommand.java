package site.markhenrick.mobilespoilers.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.doc.standard.CommandInfo;
import com.jagrosh.jdautilities.examples.doc.Author;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;

import static net.dv8tion.jda.api.Permission.*;

@CommandInfo(
	name = { "About", "Info" },
	description = "Gets information about the bot."
)
@Author("Mark Henrick")
public class AboutCommand extends Command {
	private static final String DESCRIPTION = "A temporary bot for marking spoilers from mobile, until Discord add the native ability to do that\n";
	private static final String ABOUT_LINK = "https://github.com/markhenrick/mobilespoilers";
	static final long INVITE_BITFIELD = getRaw(
		MESSAGE_ADD_REACTION, VIEW_CHANNEL, MESSAGE_WRITE, MESSAGE_MANAGE, MESSAGE_EMBED_LINKS, MESSAGE_ATTACH_FILES, MESSAGE_EXT_EMOJI);

	private final boolean showAdminInfo;

	public AboutCommand(boolean showAdminInfo) {
		this.name = "about";
		this.aliases = new String[] { "info", "invite" };
		this.help = "show info about the bot and its admin, and get an invite link to add it to your server";
		this.guildOnly = false;
		this.hidden = true;
		this.botPermissions = new Permission[]{ MESSAGE_EMBED_LINKS };
		this.showAdminInfo = showAdminInfo;
	}

	@Override
	protected void execute(CommandEvent event) {
		event.reply(getEmbed(event, showAdminInfo));
	}

	static MessageEmbed getEmbed(CommandEvent event, boolean showAdminInfo) {
		var botId = event.getSelfUser().getId();
		var guildCount = event.getClient().getTotalGuilds();

		var embed = new EmbedBuilder();
		embed.setTitle("Mobile Spoilers", ABOUT_LINK);
		embed.appendDescription(DESCRIPTION);
		embed.appendDescription("Click the title above for more info");
		embed.addField("Servers", Integer.toString(guildCount), true);
		if (showAdminInfo) {
			var ownerId = event.getClient().getOwnerId();
			var owner = event.getJDA().getUserById(ownerId);
			var ownerName = owner != null ? owner.getAsTag() : null;
			if (ownerName != null) {
				embed.addField("Admin", ownerName, true);
			}
		}
		embed.addField("Invite link", String.format("[`Click here`](%s)", getInviteLink(botId)), true);
		return embed.build();
	}

	public static String getInviteLink(String botId) {
		return String.format("https://discordapp.com/oauth2/authorize?client_id=%s&scope=bot&permissions=%s", botId, INVITE_BITFIELD);
	}
}
