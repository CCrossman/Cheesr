package com.crossman;

import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

public class PageNavigatorPanel extends Panel {
	public PageNavigatorPanel(String id) {
		super(id);

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
	}
}
