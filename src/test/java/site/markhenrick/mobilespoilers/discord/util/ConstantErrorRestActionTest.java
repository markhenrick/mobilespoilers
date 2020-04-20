package site.markhenrick.mobilespoilers.discord.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class ConstantErrorRestActionTest {
	private static final RuntimeException EXPECTED_EXCEPTION = new RuntimeException("message");
	private ConstantErrorRestAction<String> restAction;

	@BeforeEach
	void setUp() {
		restAction = new ConstantErrorRestAction<>(null, EXPECTED_EXCEPTION);
	}

	@Nested
	class Queue {
		@Test
		void usesCallback() {
			var callbackExecuted = new AtomicBoolean(false);
			restAction.queue(success -> fail(), e -> callbackExecuted.set(true));
			//noinspection ConstantConditions
			assertThat(callbackExecuted).isTrue();
		}

		@Test
		void suppliesCorrectValue() {
			restAction.queue(success -> {}, e -> assertThat(e).isEqualTo(EXPECTED_EXCEPTION));
		}

		@SuppressWarnings("JUnitTestMethodWithNoAssertions")
		@Test
		void allowsNullCallback() {
			restAction.queue(null, null);
		}
	}

	@Nested
	class Complete {
		@Test
		void throwsException() {
			assertThrows(RuntimeException.class, () -> restAction.complete(true));
			assertThrows(RuntimeException.class, () -> restAction.complete(false));
		}

		@Test
		void throwsRightException() {
			try {
				restAction.complete(true);
			} catch (RuntimeException e) {
				assertThat(e).isEqualTo(EXPECTED_EXCEPTION);
			}
		}
	}

	@Nested
	class Submit {
		@Test
		void failsFuture() throws Exception {
			var callbackExecuted = new AtomicBoolean(false);
			var future = restAction.submit(true).exceptionally(e -> {
				callbackExecuted.set(true);
				return "Callback ran";
			});
			assertThat(future.get()).isEqualTo("Callback ran");
		}
	}
}
