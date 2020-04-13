package site.markhenrick.mobilespoilers.util;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BarrierTest {
	@Test
	void runsCallback() {
		final var callbackRan = new AtomicBoolean(false);
		final var barrier = new Barrier<Integer>(1, unused -> callbackRan.set(true));
		barrier.addResult(1);
		//noinspection ConstantConditions
		assertThat(callbackRan).isTrue();
	}

	@Test
	void suppliesResult() {
		final var barrier = new Barrier<Integer>(2, results -> assertThat(results).isEqualTo(List.of(1, 2)));
		barrier.addResult(1);
		barrier.addResult(2);
	}

	@Test
	void validatesLimit() {
		//noinspection ResultOfObjectAllocationIgnored
		assertThrows(IllegalArgumentException.class, () -> new Barrier<Integer>(-1, unused -> {}));
	}

	@Test
	void runsCallbackImmediatelyIfLimitIsZero() {
		final var callbackRan = new AtomicBoolean(false);
		//noinspection ResultOfObjectAllocationIgnored
		new Barrier<Integer>(0, results -> {
			callbackRan.set(true);
			assertThat(results).isEqualTo(Collections.emptyList());
		});
		//noinspection ConstantConditions
		assertThat(callbackRan).isTrue();
	}

	@Test
	void throwsExceptionOnOverflow() {
		final var barrier = new Barrier<Integer>(2, unused -> {});
		barrier.addResult(1);
		barrier.addResult(2);
		assertThrows(IllegalStateException.class, () -> barrier.addResult(3));
	}
}
