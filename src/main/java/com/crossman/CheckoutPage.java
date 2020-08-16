package com.crossman;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.math.BigDecimal;

public class CheckoutPage extends CheesrPage implements IRequireAuthorization {

	public CheckoutPage(PageParameters pageParameters) {
		super(pageParameters);

		add(new FeedbackPanel("feedback"));

		Form form = new Form("form");
		try (Connection c = WicketApplication.getInjector().getInstance(Sql2o.class).open()) {
			Address address = c.createQuery("SELECT name, street, city, state, zip from addresses where username = :usr and kind = 'HOME'")
					.addParameter("usr", getCheesrSession().getUsername())
					.executeAndFetchFirst(AddressResultSetHandler.instance);
			if (address == null || address.isNil()) {
				setResponsePage(ProfilePage.class);
				return;
			}

			form.add(new Label("name", address.getName()));
			form.add(new Label("street", address.getStreet()));
			form.add(new Label("city", address.getCity()));
			form.add(new Label("state", address.getState()));
			form.add(new Label("zip", address.getZip()));
		}
		add(form);

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
