package com.crossman;

import com.crossman.v1.Cheese;
import com.crossman.v2.CheesrProduct;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.strategies.page.SimplePageAuthorizationStrategy;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.file.IResourceFinder;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.UrlResourceStream;
import org.sql2o.Connection;
import org.sql2o.ResultSetHandler;
import org.sql2o.Sql2o;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@NoArgsConstructor
public class WicketApplication extends WebApplication {
	@Getter
	private static final Injector injector = Guice.createInjector(new WicketApplicationModule());

	private static final Sql2o sql2o = injector.getInstance(Sql2o.class);

	public static WicketApplication get() {
		return (WicketApplication) Application.get();
	}

	public Class<?> getHomePage() {
		return LoginPage.class;
	}

	@Override
	protected void init() {
		// retrieve resources from the /resource directory when deployed to Tomcat
		final IResourceFinder defaultResourceFinder = getResourceFinder();
		getResourceSettings().setResourceFinder(new IResourceFinder() {
			@Override
			public IResourceStream find(Class clazz, String pathname) {
				final String name = pathname.substring(pathname.lastIndexOf('/') + 1);
				final URL resource = Thread.currentThread().getContextClassLoader().getResource(name);
				if (resource == null) {
					return defaultResourceFinder.find(clazz,pathname);
				}
				return new UrlResourceStream(resource);
			}
		});

		// this is how we authorize
		getSecuritySettings().setAuthorizationStrategy(new SimplePageAuthorizationStrategy(IRequireAuthorization.class, LoginPage.class) {
			@Override
			protected boolean isAuthorized() {
				return ((CheesrSession)Session.get()).getUsername() != null;
			}
		});
	}

	@Override
	public Session newSession(Request request, Response response) {
		return new CheesrSession(request);
	}

	public List<CheesrProduct> getProducts() {
		try (Connection connection = sql2o.open()) {
			return connection.createQuery("SELECT name, price from cheeses")
					.executeAndFetch(new ResultSetHandler<CheesrProduct>() {
						@Override
						public CheesrProduct handle(ResultSet resultSet) throws SQLException {
							return new CheesrProduct(CheesrProduct.Type.CHEESE, resultSet.getString("name"), resultSet.getBigDecimal("price"));
						}
					});
		}
	}
}
