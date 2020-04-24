package site.markhenrick.mobilespoilers.discord.services;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import site.markhenrick.mobilespoilers.MobileSpoilersConfig;
import site.markhenrick.mobilespoilers.dal.SpoilerRepository;

import static net.dv8tion.jda.api.Permission.*;

@Service
public class BotInfoService {
	private static final String DESCRIPTION = "A temporary bot for marking spoilers from mobile, until Discord add the native ability to do that\n";
	private static final String ABOUT_LINK = "https://github.com/markhenrick/mobilespoilers";
	private static final long INVITE_BITFIELD = getRaw(
		MESSAGE_ADD_REACTION, VIEW_CHANNEL, MESSAGE_WRITE, MESSAGE_MANAGE, MESSAGE_EMBED_LINKS, MESSAGE_ATTACH_FILES, MESSAGE_EXT_EMOJI);

	private static final Logger LOG = LoggerFactory.getLogger(BotInfoService.class);

	private final MobileSpoilersConfig config;
	private final SpoilerRepository repo;

	@Autowired
	@Lazy // Used to break a cycle. TODO revisit if this is the best solution
	private JDAHolder jdaHolder;

	public BotInfoService(MobileSpoilersConfig config, SpoilerRepository repo) {
		this.config = config;
		this.repo = repo;
	}

	public String getInviteLink() {
		return String.format("https://discordapp.com/oauth2/authorize?client_id=%s&scope=bot&permissions=%s",
			jdaHolder.getJda().getSelfUser().getId(), INVITE_BITFIELD);
	}

	public MessageEmbed getAboutEmbed() {
		var jda = jdaHolder.getJda();
		var client = jdaHolder.getCommandClient();
		var embed = new EmbedBuilder();
		embed.setTitle("Mobile Spoilers", ABOUT_LINK);
		embed.appendDescription(DESCRIPTION);
		embed.appendDescription("Click the title above for more info");
		embed.addField("Servers", Integer.toString(client.getTotalGuilds()), true);
		embed.addField("Active spoilers", Long.toString(repo.count()), true);
		embed.addField("Latency", String.format("%s ms", jda.getGatewayPing()), true);
		if (config.isShowAdminInfo()) {
			var owner = jda.getUserById(client.getOwnerId());
			var ownerName = owner != null ? owner.getAsTag() : null;
			if (ownerName != null) {
				embed.addField("Admin", ownerName, true);
			} else {
				LOG.warn("Could not find owner user");
			}
		}
		embed.addField("Invite link", String.format("[`Click here`](%s)", getInviteLink()), true);
		return embed.build();
	}
}
