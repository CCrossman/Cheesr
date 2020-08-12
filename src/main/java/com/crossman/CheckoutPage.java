package com.crossman;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

public class CheckoutPage extends CheesrPage {

	public CheckoutPage() {
		add(new FeedbackPanel("feedback"));

		Form form = new Form("form");
		add(form);

		Address address = getCart().getBillingAddress();
		form.add(new TextField("name", new PropertyModel(address, "name")).setRequired(true));
		form.add(new TextField("street", new PropertyModel(address, "street")).setRequired(true));
		form.add(new TextField("city", new PropertyModel(address, "city")).setRequired(true));
		form.add(new TextField("state", new PropertyModel(address, "state")).setRequired(true));
		form.add(new TextField("zip", new PropertyModel(address, "zip")).setRequired(true));

		form.add(new Link("cancel") {
			@Override
			public void onClick() {
				setResponsePage(HomePage.class);
			}
		});

		form.add(new Link("order") {
			@Override
			public void onClick() {
				Cart cart = getCart();

				// TODO: charge customer
				// TODO: ship cheeses
				// clean out shopping cart
				cart.getCheeses().clear();

				// return to front page
				setResponsePage(HomePage.class);
			}
		});

		add(new ShoppingCartPanel("shoppingcart", getCart()));
	}
}
