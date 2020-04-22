package site.markhenrick.mobilespoilers.discord.service;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import site.markhenrick.mobilespoilers.MobileSpoilersConfig;
import site.markhenrick.mobilespoilers.discord.JDAHolder;

import static net.dv8tion.jda.api.Permission.*;

@Service
public class BotInfoService {
	private static final String DESCRIPTION = "A temporary bot for marking spoilers from mobile, until Discord add the native ability to do that\n";
	private static final String ABOUT_LINK = "https://github.com/markhenrick/mobilespoilers";
	private static final long INVITE_BITFIELD = getRaw(
		MESSAGE_ADD_REACTION, VIEW_CHANNEL, MESSAGE_WRITE, MESSAGE_MANAGE, MESSAGE_EMBED_LINKS, MESSAGE_ATTACH_FILES, MESSAGE_EXT_EMOJI);

	private final MobileSpoilersConfig config;

	@Autowired
	@Lazy // Used to break a cycle. TODO revisit if this is the best solution
	private JDAHolder jdaFacade;

	public BotInfoService(MobileSpoilersConfig config) {
		this.config = config;
	}

	public String getInviteLink() {
		return String.format("https://discordapp.com/oauth2/authorize?client_id=%s&scope=bot&permissions=%s",
			jdaFacade.getJda().getSelfUser().getId(), INVITE_BITFIELD);
	}

	public MessageEmbed getAboutEmbed() {
		var embed = new EmbedBuilder();
		embed.setTitle("Mobile Spoilers", ABOUT_LINK);
		embed.appendDescription(DESCRIPTION);
		embed.appendDescription("Click the title above for more info");
		embed.addField("Servers", Integer.toString(jdaFacade.getCommandClient().getTotalGuilds()), true);
		if (config.isShowAdminInfo()) {
			var owner = jdaFacade.getJda().getUserById(jdaFacade.getCommandClient().getOwnerId());
			var ownerName = owner != null ? owner.getAsTag() : null;
			if (ownerName != null) {
				embed.addField("Admin", ownerName, true);
			}
		}
		embed.addField("Invite link", String.format("[`Click here`](%s)", getInviteLink()), true);
		return embed.build();
	}
}
