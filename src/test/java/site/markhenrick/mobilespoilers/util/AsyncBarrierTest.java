package site.markhenrick.mobilespoilers.util;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class AsyncBarrierTest {
	@Test
	void shouldRunsCallback() {
		var callbackRan = new AtomicBoolean(false);
		var barrier = new AsyncBarrier<Integer, Void>(1, unused -> callbackRan.set(true), e -> fail());
		barrier.addResult(1);
		//noinspection ConstantConditions
		assertThat(callbackRan).isTrue();
	}

	@Test
	void shouldSupplyResult() {
		var barrier = new AsyncBarrier<Integer, Void>(2, results -> assertThat(results).isEqualTo(List.of(1, 2)), e -> fail());
		barrier.addResult(1);
		barrier.addResult(2);
	}

	@Test
	void shouldValidateLimit() {
		//noinspection ResultOfObjectAllocationIgnored
		assertThrows(IllegalArgumentException.class, () -> new AsyncBarrier<Void, Void>(-1, unused -> fail(), e -> fail()));
	}

	@Test
	void shouldRunCallbackImmediatelyIfLimitIsZero() {
		var callbackRan = new AtomicBoolean(false);
		//noinspection ResultOfObjectAllocationIgnored
		new AsyncBarrier<Void, Void>(0, results -> {
			callbackRan.set(true);
			assertThat(results).isEqualTo(Collections.emptyList());
		}, e -> fail());
		//noinspection ConstantConditions
		assertThat(callbackRan).isTrue();
	}

	@Test
	void shouldRunErrorCallback() {
		var callbackRan = new AtomicBoolean(false);
		var barrier = new AsyncBarrier<Void, Integer>(1, unused -> fail(), e -> callbackRan.set(true));
		barrier.error(1);
		//noinspection ConstantConditions
		assertThat(callbackRan).isTrue();
	}

	@Test
	void shouldSupplyError() {
		var barrier = new AsyncBarrier<Integer, Integer>(1, unused -> fail(), e -> assertThat(e).isEqualTo(1));
		barrier.error(1);
	}

	@Test
	void shouldIgnoreSubsequentErrors() {
		var timesCallbackRan = new AtomicInteger(0);
		var barrier = new AsyncBarrier<Void, Integer>(1, unused -> fail(), e -> timesCallbackRan.incrementAndGet());
		barrier.error(1);
		barrier.error(2);
		assertThat(timesCallbackRan).hasValue(1);
	}

	@Test
	void shouldNotRunSuccessCallbackAfterError() {
		var barrier = new AsyncBarrier<Integer, Integer>(1, unused -> fail(), e -> {});
		barrier.error(1);
		barrier.addResult(1);
	}

	@Test
	void shouldThrowExceptionOnOverflow() {
		var barrier = new AsyncBarrier<Integer, Void>(2, unused -> {}, e -> fail());
		barrier.addResult(1);
		barrier.addResult(2);
		assertThrows(IllegalStateException.class, () -> barrier.addResult(3));
	}

	@Test
	void shouldThrowExceptionOnErrorWhenFull() {
		var barrier = new AsyncBarrier<Integer, Integer>(1, unused -> {}, e -> fail());
		barrier.addResult(1);
		assertThrows(IllegalStateException.class, () -> barrier.error(1));
	}
}
