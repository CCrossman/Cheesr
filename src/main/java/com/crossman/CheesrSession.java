package com.crossman;

import lombok.Getter;
import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebSession;

public class CheesrSession extends WebSession {
	@Getter
	private Cart cart = new Cart();

	protected CheesrSession(Request request) {
		super(request);
	}
}
