package org.joo.scorpius.support.deferred;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

public class DeferredObject<D, F extends Throwable> implements Deferred<D, F>, Promise<D, F> {

	private volatile D result;

	private volatile F failedCause;

	private volatile DeferredStatus status;

	private volatile DoneCallback<D> doneCallback;

	private volatile FailCallback<F> failureCallback;

	private AtomicBoolean done;

	private AtomicBoolean alert;

	public DeferredObject() {
		this.done = new AtomicBoolean(false);
		this.alert = new AtomicBoolean(false);
	}

	@Override
	public Deferred<D, F> resolve(D result) {
		if (!done.compareAndSet(false, true))
			throw new IllegalStateException("Deferred is already resolved or rejected");
		this.result = result;
		this.status = DeferredStatus.DONE;
		this.onComplete(result);
		return this;
	}

	@Override
	public Deferred<D, F> reject(F failedCause) {
		if (!done.compareAndSet(false, true))
			throw new IllegalStateException("Deferred is already resolved or rejected");
		this.failedCause = failedCause;
		this.status = DeferredStatus.REJECTED;
		this.onFail(failedCause);
		return this;
	}

	private void onComplete(D result) {
		if (doneCallback != null) {
			if (alert.compareAndSet(false, true))
				doneCallback.onDone(result);
		}
	}

	private void onFail(F failedCause) {
		if (failureCallback != null) {
			if (alert.compareAndSet(false, true))
				failureCallback.onFail(failedCause);
		}
	}

	@Override
	public Promise<D, F> promise() {
		return this;
	}

	@Override
	public Promise<D, F> done(DoneCallback<D> callback) {
		doneCallback = callback;
		if (status == DeferredStatus.DONE) {
			if (alert.compareAndSet(false, true))
				callback.onDone(result);
		}
		return this;
	}

	@Override
	public Promise<D, F> fail(FailCallback<F> callback) {
		this.failureCallback = callback;
		if (status == DeferredStatus.REJECTED) {
			if (alert.compareAndSet(false, true))
				callback.onFail(failedCause);
		}
		return this;
	}
}

enum DeferredStatus {
	DONE, REJECTED;
}