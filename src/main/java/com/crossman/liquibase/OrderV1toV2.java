package com.crossman.liquibase;

import com.crossman.WicketApplicationModule;
import com.crossman.v2.CheesrProduct;
import com.crossman.v2.Order;
import com.crossman.v2.StoredOrder;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import org.sql2o.Connection;
import org.sql2o.ResultSetHandler;
import org.sql2o.ResultSetIterable;
import org.sql2o.Sql2o;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public final class OrderV1toV2 implements CustomTaskChange {
	private static final Injector injector = Guice.createInjector(new WicketApplicationModule());

	private ResourceAccessor resourceAccessor;

	@Override
	public void execute(Database database) throws CustomChangeException {
		try {
			final Gson gson = injector.getInstance(Gson.class);
			final Sql2o sql2o = injector.getInstance(Sql2o.class);

			try (Connection c = sql2o.open()) {
				try(ResultSetIterable<StoredOrder> orders = c.createQuery("SELECT id, json, shipped FROM orders WHERE json ->> 'version' = null").executeAndFetchLazy(new StoredOrderResultSetHandler(gson))) {
					for (StoredOrder so : orders) {
						String json = gson.toJson(new Order(Order.Version.v2, so.getUsername(), so.getAddress(), so.getProducts(), so.getPrice()));
						c.createQuery("UPDATE orders SET json = :json WHERE id = :id")
								.addParameter("json", json)
								.addParameter("id", so.getId())
								.executeUpdate();
					}
				}
			}
		} catch (Exception e) {
			throw new CustomChangeException(e);
		}
	}

	@Override
	public String getConfirmationMessage() {
		return "Ran OrderV1toV2 custom change";
	}

	@Override
	public void setUp() throws SetupException {

	}

	@Override
	public void setFileOpener(ResourceAccessor resourceAccessor) {
		this.resourceAccessor = resourceAccessor;
	}

	@Override
	public ValidationErrors validate(Database database) {
		return null;
	}

	private static final class StoredOrderResultSetHandler implements ResultSetHandler<StoredOrder> {
		private final Gson gson;

		public StoredOrderResultSetHandler(Gson gson) {
			this.gson = gson;
		}

		@Override
		public StoredOrder handle(ResultSet rs) throws SQLException {
			long id = rs.getLong("id");
			com.crossman.v1.Order order = gson.fromJson(rs.getString("json"), com.crossman.v1.Order.class);
			boolean shipped = rs.getBoolean("shipped");

			List<CheesrProduct> products = order.getCheeses().stream().map(ch -> new CheesrProduct(CheesrProduct.Type.CHEESE, ch.getName(), ch.getPrice())).collect(Collectors.toList());
			return new StoredOrder(id, Order.Version.v2, order.getUsername(), order.getAddress(), products, order.getPrice(), shipped);
		}
	}
}
