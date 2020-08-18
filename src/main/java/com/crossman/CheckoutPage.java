package com.crossman;

import com.google.gson.Gson;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.PropertyModel;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class CheckoutPage extends CheesrPage implements IRequireAuthorization {
	// accessed only by PropertyModel for radio buttons
	private String selected = "HOME";

	public CheckoutPage(PageParameters pageParameters) {
		super(pageParameters);

		add(new FeedbackPanel("feedback"));

		try (Connection c = WicketApplication.getInjector().getInstance(Sql2o.class).open()) {
			final List<Address> addresses = c.createQuery("SELECT kind, name, street, city, state, zip from addresses where username = :usr")
					.addParameter("usr", getCheesrSession().getUsername())
					.executeAndFetch(AddressResultSetHandler.instance);
			if (addresses == null || addresses.isEmpty()) {
				setResponsePage(ProfilePage.class);
				return;
			}

			ListView listView = new ListView("addressList", addresses) {
				@Override
				protected void populateItem(ListItem item) {
					Address address = (Address) item.getModelObject();
					item.add(new Label("kind", address.getKind().name()));
					item.add(new Label("name", address.getName()));
					item.add(new Label("street", address.getStreet()));
					item.add(new Label("city", address.getCity()));
					item.add(new Label("state", address.getState()));
					item.add(new Label("zip", address.getZip()));
				}
			};

			Form form = new Form("form") {
				@Override
				protected void onSubmit() {
					final Cart cart = getCart();
					final int cheesesSold = cart.getCheeses().size();
					final BigDecimal priceSold = cart.getTotal();
					final Address address = addresses.stream().filter(addr -> addr.getKind().name().equals(selected)).findFirst().orElse(null);

					final Order order = new Order(getCheesrSession().getUsername(), address, cart.getCheeses(), priceSold);
					final String json = WicketApplication.getInjector().getInstance(Gson.class).toJson(order);
					try (Connection c = WicketApplication.getInjector().getInstance(Sql2o.class).open()) {
						c.createQuery("INSERT INTO orders (json, created, updated) VALUES (:order, now(), now())")
								.addParameter("order", json)
								.executeUpdate();
					}
					// clean out shopping cart
					cart.getCheeses().clear();

					// return to front page
					PageParameters pp = new PageParameters();
					pp.add("message", "Sold " + cheesesSold + " cheeses for $" + priceSold);
					setResponsePage(HomePage.class, pp);
				}
			};
			form.add(listView);

			RadioChoice radioChoice = new RadioChoice("radio", new PropertyModel(this, "selected"), addresses.stream().map(addr -> addr.getKind().name()).collect(Collectors.toList()));
			form.add(radioChoice);

			form.add(new Link("cancel") {
				@Override
				public void onClick() {
					setResponsePage(HomePage.class);
				}
			});
			add(form);
		}

		add(new ShoppingCartPanel("shoppingcart", getCart()));
	}
}
