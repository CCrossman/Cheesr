package com.crossman;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;

import java.math.BigDecimal;

public class CheckoutPage extends CheesrPage implements IRequireAuthorization {

	public CheckoutPage(PageParameters pageParameters) {
		super(pageParameters);

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
				final Cart cart = getCart();
				final int cheesesSold = cart.getCheeses().size();
				final BigDecimal priceSold = cart.getTotal();

				// TODO: charge customer
				System.err.println("charging customer '" + getCheesrSession().getUsername() + "'");

				// TODO: ship cheeses
				// clean out shopping cart
				cart.getCheeses().clear();

				// return to front page
				PageParameters pp = new PageParameters();
				pp.add("message", "Sold " + cheesesSold + " cheeses for $" + priceSold);
				setResponsePage(HomePage.class, pp);
			}
		});

		add(new ShoppingCartPanel("shoppingcart", getCart()));
	}
}
