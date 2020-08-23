package com.crossman;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

public class PageNavigatorPanel extends Panel {
	public PageNavigatorPanel(String id) {
		super(id);

		add(new Link("home") {
			@Override
			public void onClick() {
				setResponsePage(HomePage.class);
			}
		});

		add(new Link("orders") {
			@Override
			public void onClick() {
				setResponsePage(OrderPage.class);
			}
		});

		add(new Link("profile") {
			@Override
			public void onClick() {
				setResponsePage(ProfilePage.class);
			}
		});

		add(new Link("signup") {
			@Override
			public void onClick() {
				setResponsePage(SignupPage.class);
			}
		});

		add(new Link("users") {
			@Override
			public void onClick() {
				setResponsePage(UserPage.class);
			}

			@Override
			public boolean isVisible() {
				final CheesrSession session = getCheesrSession();
				return session != null && session.getUser() != null && session.getUser().isAdmin();
			}
		});

		add(new Link("logoff") {
			@Override
			public void onClick() {
				getCheesrSession().setUser(null);
				setResponsePage(LoginPage.class);
			}
		});
	}

	private CheesrSession getCheesrSession() {
		return ((CheesrPage) PageNavigatorPanel.this.getParent()).getCheesrSession();
	}
}
