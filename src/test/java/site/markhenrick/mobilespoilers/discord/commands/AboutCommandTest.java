package site.markhenrick.mobilespoilers.discord.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AboutCommandTest {
	private static final String botId = "111111111111111111";

	@Nested
	class GetInviteLink {
		private String inviteLink;

		@BeforeEach
		void setUp() {
			this.inviteLink = AboutCommand.getInviteLink(botId);
		}

		@Test
		void isADiscordLink() {
			assertThat(inviteLink)
				.startsWith("https://discordapp.com")
				.doesNotContain(" ")
				.doesNotContain("\n")
				.doesNotContain("\r")
				.doesNotContain("\t");
		}

		@Test
		void containsBotId() {
			assertThat(inviteLink).contains(botId);
		}

		@Test
		void containsPermissionsBitfield() {
			assertThat(inviteLink).contains(Long.toString(AboutCommand.INVITE_BITFIELD));
		}

		@Test
		void doesNotContainLongL() {
			assertThat(inviteLink).doesNotMatch("\\d{18}[lL]");
		}
	}
}
