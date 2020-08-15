package com.crossman;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

public class LoginPage extends WebPage {

	public LoginPage() {
		this(null);
	}

	public LoginPage(Message message) {
		if (message != null) {
			message.apply(this);
		}

		add(new FeedbackPanel("feedback"));
	}
}
