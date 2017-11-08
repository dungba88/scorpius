package org.joo.scorpius.trigger;

import org.jdeferred.Deferred;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;
import org.joo.scorpius.ApplicationContext;
import org.joo.scorpius.support.BaseRequest;
import org.joo.scorpius.support.BaseResponse;
import org.joo.scorpius.support.TriggerExecutionException;

public class DefaultTriggerExecutionContext implements TriggerExecutionContext {
	
	private TriggerConfig config;
	
	private BaseRequest request;
	
	private ApplicationContext applicationContext;
	
	private TriggerExecutionStatus status;
	
	private Deferred<BaseResponse, TriggerExecutionException, Object> deferred;

	public DefaultTriggerExecutionContext(TriggerConfig config, BaseRequest request, ApplicationContext applicationContext) {
		this.config = config;
		this.request = request;
		this.applicationContext = applicationContext;
		this.status = TriggerExecutionStatus.CREATED;
		this.deferred = new DeferredObject<>();
	}
	
	public void pending() {
		status = TriggerExecutionStatus.PENDING;
	}
	
	public void execute() {
		if (status == TriggerExecutionStatus.EXECUTING || status == TriggerExecutionStatus.FINISHED) {
			throw new IllegalAccessError("Trigger is already running or finished");
		}
		try {
			config.getTrigger().execute(this);
		} catch (TriggerExecutionException e) {
			reject(e);
		}
	}
	
	public void finish(BaseResponse response) {
		if (status == TriggerExecutionStatus.FINISHED)
			throw new IllegalAccessError("Trigger is already finished");
		deferred.resolve(response);
	}
	
	public void reject(TriggerExecutionException ex) {
		if (status == TriggerExecutionStatus.FINISHED)
			throw new IllegalAccessError("Trigger is already finished");
		deferred.reject(ex);
	}
	
	public Promise<BaseResponse, TriggerExecutionException, Object> promise() {
		return deferred.promise();
	}

	public TriggerConfig getConfig() {
		return config;
	}

	public BaseRequest getRequest() {
		return request;
	}

	public TriggerExecutionStatus getStatus() {
		return status;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}