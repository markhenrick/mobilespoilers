package site.markhenrick.mobilespoilers.discord.services;

import net.dv8tion.jda.api.entities.MessageChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import site.markhenrick.mobilespoilers.dal.Spoiler;
import site.markhenrick.mobilespoilers.dal.SpoilerRepository;
import site.markhenrick.mobilespoilers.discord.util.BotException;
import site.markhenrick.mobilespoilers.discord.util.ConstantErrorRestAction;
import site.markhenrick.mobilespoilers.discord.util.ConstantRestAction;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.quality.Strictness.LENIENT;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = LENIENT)
class DeletionServiceTest {
	@Mock
	private JDAHolder jdaHolder;
	@Mock
	private SpoilerRepository repo;
	@Mock
	private MessageChannel channel;
	private DeletionService service;

	@BeforeEach
	void setUp() {
		var restActionFactory = new RestActionFactory(jdaHolder);

		Mockito.when(channel.deleteMessageById(1L))
			.thenReturn(new ConstantRestAction<>(null, null));
		Mockito.when(jdaHolder.getMessageChannelById(3L)).thenReturn(Optional.of(channel));

		var spoiler = new Spoiler();
		spoiler.setMessageId(1L);
		spoiler.setUserId(2L);
		spoiler.setChannelId(3L);
		Mockito.when(repo.findById(1L)).thenReturn(Optional.of(spoiler));

		this.service = new DeletionService(jdaHolder, restActionFactory, repo);
	}

	@Nested
	class HappyPath {
		@BeforeEach
		void doTest() {
			testCase(2L, 1L);
		}

		@Test
		void shouldDeleteRecord() {
			Mockito.verify(repo).deleteById(1L);
		}

		@Test
		void shouldDeleteMessage() {
			//noinspection ResultOfMethodCallIgnored
			Mockito.verify(channel).deleteMessageById(1L);
		}
	}

	@Nested
	class ErrorHandling {
		@Test
		void shouldHandleUnknownSpoiler() {
			var e = assertThrows(BotException.class, () -> testCase(2L, 5L));
			assertThat(e.getMessage()).contains("no record");
		}

		@Test
		void shouldHandleWrongUser() {
			var e = assertThrows(BotException.class, () -> testCase(5L, 1L));
			assertThat(e.getMessage()).contains("user");
		}

		@Test
		void shouldHandleUnavailableChannel() {
			Mockito.when(jdaHolder.getMessageChannelById(3L)).thenReturn(Optional.empty());
			var e = assertThrows(BotException.class, () -> testCase(2L, 1L));
			assertThat(e.getMessage()).contains("channel");
		}

		@Test
		void shouldPassOnUnexpectedError() {
			Mockito.when(channel.deleteMessageById(1L))
				.thenReturn(new ConstantErrorRestAction<>(null, new NullPointerException("example")));
			var e = assertThrows(NullPointerException.class, () -> testCase(2L, 1L));
			assertThat(e.getMessage()).isEqualTo("example");
		}
	}

	private void testCase(long userId, long messageId) {
		assertThat(service.tryDeleteMessage(userId, messageId).complete()).isNotNull();
	}
}
