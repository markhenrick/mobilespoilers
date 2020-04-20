package site.markhenrick.mobilespoilers.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Accept a defined number of results and then run a callback
 */
public class AsyncBarrier<T, E> {
	private final int limit;
	private final List<T> results;
	private final Consumer<? super List<T>> onSuccess;
	private final Consumer<? super E> onError;
	private boolean errorOccurred;

	/**
	 * Accept `limit` results and then run `onSuccess`
	 * @param limit The number of results to expect. If this is 0, `onSuccess` is run immediately on an empty list
	 * @param onSuccess The consumer to run when the barrier is full. The supplied list is never null
	 */
	public AsyncBarrier(int limit, Consumer<? super List<T>> onSuccess, Consumer<? super E> onError) {
		this.limit = limit;
		this.onSuccess = onSuccess;
		this.onError = onError;
		this.errorOccurred = false;
		this.results = new ArrayList<>(limit);
		if (limit == 0) {
			onSuccess.accept(this.results);
		}
	}

	/**
	 * Add a result. If the barrier becomes full, the callback is run (in the same thread as the call).
	 * Otherwise, control returns immediately
	 * @param result The result to add
	 * @throws IllegalStateException If the barrier is already full
	 */
	public synchronized void addResult(T result) {
		if (isFull()) throw new IllegalStateException("Barrier is full or in error state");
		results.add(result);
		if (isFull() && !errorOccurred) {
			onSuccess.accept(results);
		}
	}

	/**
	 * Raise an error, causing the error callback to be run
	 * @throws IllegalStateException If the barrier is full
	 */
	public synchronized void error(E error) {
		if (isFull()) throw new IllegalStateException("Cannot raise error when barrier is full");
		if (!errorOccurred) {
			errorOccurred = true;
			onError.accept(error);
		}
	}

	private boolean isFull() {
		return results.size() == limit;
	}
}
