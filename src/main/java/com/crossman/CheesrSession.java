package com.crossman;

import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebSession;

public class CheesrSession extends WebSession {
	@Getter
	private final Cart cart = new Cart();

	@Getter
	@Setter
	private String username;

	protected CheesrSession(Request request) {
		super(request);
	}
}
