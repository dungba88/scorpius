package org.joo.scorpius.test.vertx.sample;

import org.joo.scorpius.test.vertx.trigger.TriggerManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

public class MessageController implements Handler<RoutingContext> {
	
	private TriggerManager triggerManager;

	public MessageController(TriggerManager triggerManager) {
		this.triggerManager = triggerManager;
	}

	public void handle(RoutingContext rc) {
		HttpServerResponse response = rc.response();
		response.putHeader("Content-Type", "application/json");

		String msgName = rc.request().getParam("name");
		String msgData = rc.getBodyAsString();
		
		triggerManager.fire(msgName, msgData, triggerResponse -> {
			if (triggerResponse == null) {
				response.end();
				return;
			}
			ObjectMapper mapper = new ObjectMapper();
			try {
				String strResponse = mapper.writeValueAsString(triggerResponse);
				response.end(strResponse);
			} catch (JsonProcessingException e) {
				rc.fail(e);
			}
		});
	}
}
