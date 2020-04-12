package site.markhenrick.mobilespoilers.discord.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class ConstantRestActionTest {
	private static final String EXPECTED_RESULT = "result";
	private ConstantRestAction<String> restAction;

	@BeforeEach
	void setUp() {
		restAction = new ConstantRestAction<>(null, EXPECTED_RESULT);
	}

	@Nested
	class Queue {
		@Test
		void usesCallback() {
			final var callbackExecuted = new AtomicBoolean(false);
			restAction.queue(actualResult -> callbackExecuted.set(true));
			//noinspection ConstantConditions
			assertThat(callbackExecuted).isTrue();
		}

		@Test
		void suppliesCorrectValue() {
			restAction.queue(actualResult -> assertThat(actualResult).isEqualTo(EXPECTED_RESULT));
		}

		@SuppressWarnings("JUnitTestMethodWithNoAssertions")
		@Test
		void allowsNullCallback() {
			restAction.queue(null);
		}
	}

	@Nested
	class Complete {
		@Test
		void returnsValue() {
			assertThat(restAction.complete(true)).isEqualTo(EXPECTED_RESULT);
			assertThat(restAction.complete(false)).isEqualTo(EXPECTED_RESULT);
		}
	}

	@Nested
	class Submit {
		@Test
		void returnsValue() throws Exception {
			assertThat(restAction.submit(true).get()).isEqualTo(EXPECTED_RESULT);
			assertThat(restAction.submit(false).get()).isEqualTo(EXPECTED_RESULT);
		}
	}
}
