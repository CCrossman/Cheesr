package com.crossman;

import com.crossman.v2.StoredUser;
import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebSession;

public class CheesrSession extends WebSession {
	@Getter
	private final Cart cart = new Cart();

	@Getter
	@Setter
	private StoredUser user;

	protected CheesrSession(Request request) {
		super(request);
	}
}
