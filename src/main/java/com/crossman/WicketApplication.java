package com.crossman;

import com.crossman.v2.CheesrProduct;
import com.crossman.v2.StoredUser;
import com.google.inject.Guice;
import com.google.inject.Injector;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.strategies.CompoundAuthorizationStrategy;
import org.apache.wicket.authorization.strategies.page.SimplePageAuthorizationStrategy;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.file.IResourceFinder;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.UrlResourceStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.ResultSetHandler;
import org.sql2o.Sql2o;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
public class WicketApplication extends WebApplication {
	private static final Logger logger = LoggerFactory.getLogger(WicketApplication.class);

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
		// run Liquibase
		try (Connection c = sql2o.open()) {
			Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(c.getJdbcConnection()));
			Liquibase liquibase = new Liquibase("dbchangelog.xml", new ClassLoaderResourceAccessor(), database);
			liquibase.update(new Contexts());
		} catch (LiquibaseException e) {
			logger.error("Problem running Liquibase changelog.", e);
			System.exit(1);
		}

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
		final CompoundAuthorizationStrategy strategy = new CompoundAuthorizationStrategy();
		strategy.add(new SimplePageAuthorizationStrategy(IRequireAuthorization.class, LoginPage.class) {
			@Override
			protected boolean isAuthorized() {
				final StoredUser storedUser = ((CheesrSession) Session.get()).getUser();
				return storedUser != null && storedUser.isActive();
			}
		});
		strategy.add(new SimplePageAuthorizationStrategy(IRequireAdminAuthorization.class, LoginPage.class) {
			@Override
			protected boolean isAuthorized() {
				final StoredUser storedUser = ((CheesrSession) Session.get()).getUser();
				return storedUser != null && storedUser.isActive() && storedUser.isAdmin();
			}
		});
		getSecuritySettings().setAuthorizationStrategy(strategy);
	}

	@Override
	public Session newSession(Request request, Response response) {
		return new CheesrSession(request);
	}

	public List<CheesrProduct> getProducts() {
		try (Connection connection = sql2o.open()) {
			return connection.createQuery("SELECT name, type, price from products")
					.executeAndFetch(new ResultSetHandler<Optional<CheesrProduct>>() {
						@Override
						public Optional<CheesrProduct> handle(ResultSet resultSet) throws SQLException {
							try {
								final CheesrProduct.Type type = CheesrProduct.Type.valueOf(resultSet.getString("type"));
								final String name = resultSet.getString("name");
								final BigDecimal price = resultSet.getBigDecimal("price");
								return Optional.of(new CheesrProduct(type, name, price));
							} catch (Exception e) {
								logger.warn("Problem loading a product.", e);
								return Optional.empty();
							}
						}
					})
					.stream().filter(o -> o.isPresent()).map(o -> o.get()).collect(Collectors.toList());
		}
	}
}
