package com.crossman;

import com.google.gson.Gson;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.sql2o.Connection;
import org.sql2o.ResultSetHandler;
import org.sql2o.Sql2o;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class OrderPage extends CheesrPage implements IRequireAuthorization {
	public OrderPage(PageParameters pageParameters) {
		super(pageParameters);

		add(new FeedbackPanel("feedback"));

		final Gson gson = WicketApplication.getInjector().getInstance(Gson.class);
		final Sql2o sql2o = WicketApplication.getInjector().getInstance(Sql2o.class);
		try (Connection c = sql2o.open()) {
			List<StoredOrder> storedOrders = c.createQuery("SELECT id, json, shipped FROM orders WHERE json ->> 'username' = :usr")
					.addParameter("usr", getCheesrSession().getUsername())
					.executeAndFetch(new ResultSetHandler<StoredOrder>() {
						@Override
						public StoredOrder handle(ResultSet resultSet) throws SQLException {
							long id = resultSet.getLong("id");
							Order order = gson.fromJson(resultSet.getString("json"), Order.class);
							boolean shipped = resultSet.getBoolean("shipped");
							return new StoredOrder(id, order.getUsername(), order.getAddress(), order.getCheeses(), order.getPrice(), shipped);
						}
					});

			ListView listView = new ListView("orders", storedOrders) {
				@Override
				protected void populateItem(ListItem item) {
					StoredOrder so = (StoredOrder)item.getModelObject();
					item.add(new Label("id", String.valueOf(so.getId())));
					item.add(new Label("address", print(so.getAddress())));
					item.add(new Label("cheeses", so.getCheeses().stream().map(Cheese::getName).collect(Collectors.joining(", "))));
					item.add(new Label("price", String.valueOf(so.getPrice())));
					item.add(new Label("shipped", String.valueOf(so.isShipped())));
				}
			};
			add(listView);
		}
	}

	private static String print(Address address) {
		return address.getStreet() + ", " + address.getCity() + ", " + address.getState() + ", " + address.getZip();
	}
}