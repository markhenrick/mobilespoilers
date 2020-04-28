package site.markhenrick.mobilespoilers.discord.services;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import site.markhenrick.mobilespoilers.discord.util.BotException;
import site.markhenrick.mobilespoilers.discord.util.ConstantErrorRestAction;
import site.markhenrick.mobilespoilers.discord.util.ConstantRestAction;
import site.markhenrick.mobilespoilers.testutil.MobileSpoilersSpringTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MobileSpoilersSpringTest
@DataSet("singleSpoiler.yml")
class DeletionServiceTest {
	@MockBean
	private JDAHolder jdaHolder;
	@Mock
	private MessageChannel channel;
	@Autowired
	private DeletionService service;

	@BeforeEach
	void setUp() {
		Mockito.when(channel.deleteMessageById(1L))
			.thenReturn(new ConstantRestAction<>(null, null));
		Mockito.when(jdaHolder.getMessageChannelById(3L)).thenReturn(Optional.of(channel));
	}

	@Test
	@ExpectedDataSet("noSpoilers.yml")
	void shouldDeleteRecord() {
		testCase(2L, 1L);
	}

	@Test
	void shouldDeleteMessage() {
		testCase(2L, 1L);
		//noinspection ResultOfMethodCallIgnored
		Mockito.verify(channel).deleteMessageById(1L);
	}
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

	private void testCase(long userId, long messageId) {
		assertThat(service.tryDeleteMessage(userId, messageId).complete()).isNotNull();
	}
}
