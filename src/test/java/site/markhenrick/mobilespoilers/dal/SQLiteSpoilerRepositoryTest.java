package site.markhenrick.mobilespoilers.dal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SQLiteSpoilerRepositoryTest {
	private SpoilerRepository repo;

	@BeforeEach
	void setUp() throws IOException {
		final var tempDbFile = File.createTempFile("mobilespoilers-test-", ".sqlite3");
		tempDbFile.deleteOnExit();
		this.repo = new SQLiteSpoilerRepository(tempDbFile.getPath());
	}

	@Test
	void persistence() {
		repo.recordSpoiler("1", "2", "3");
		final var retrievedSpoiler1 = repo.getSpoiler("1");
		assertThat(retrievedSpoiler1).isNotNull();
		assertThat(retrievedSpoiler1.getMessageId()).isEqualTo("1");
		assertThat(retrievedSpoiler1.getChannelId()).isEqualTo("2");
		assertThat(retrievedSpoiler1.getUserId()).isEqualTo("3");
		repo.deleteSpoiler(retrievedSpoiler1);
		final var retrievedSpoiler2 = repo.getSpoiler("1");
		assertThat(retrievedSpoiler2).isNull();
	}

	@SuppressWarnings("JUnitTestMethodWithNoAssertions")
	@Test
	void swallowsExceptionsWhenDeleting() {
		repo.deleteSpoiler(null);
	}
}
