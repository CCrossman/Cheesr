package com.crossman;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;

/**
 * Homepage
 */
public class HomePage extends CheesrPage {

	public HomePage() {
		this(null);
	}

	public HomePage(String message) {
		add(new FeedbackPanel("feedback"));
		add(new ListView("cheeses", getCheeses()) {
			@Override
			protected void populateItem(ListItem item) {
				Cheese cheese = (Cheese) item.getModelObject();
				item.add(new Label("name", cheese.getName()));
				item.add(new Label("price", "$" + cheese.getPrice()));
				item.add(new Link("add", item.getModel()) {
					@Override
					public void onClick() {
						Cheese selected = (Cheese)getModelObject();
						getCart().getCheeses().add(selected);
					}
				});
			}
		});
		add(new ShoppingCartPanel("shoppingcart", getCart()));
		add(new Link("checkout") {
			@Override
			public void onClick() {
				HomePage.this.setResponsePage(new CheckoutPage());
			}

			@Override
			public boolean isVisible() {
				return !getCart().getCheeses().isEmpty();
			}
		});

		if (message != null) {
			info(message);
		}
	}
}
