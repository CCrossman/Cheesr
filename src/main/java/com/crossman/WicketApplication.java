package com.crossman;

import lombok.NoArgsConstructor;
import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.file.IResourceFinder;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.UrlResourceStream;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.net.URL;
import java.util.List;

@NoArgsConstructor
public class WicketApplication extends WebApplication {
	private final Sql2o sql2o = new Sql2o("jdbc:postgresql://localhost:5432/cheesr", System.getenv("POSTGRES_USERNAME"), System.getenv("POSTGRES_PASSWORD"));

	public static WicketApplication get() {
		return (WicketApplication) Application.get();
	}

	public Class<?> getHomePage() {
		return HomePage.class;
	}

	public List<Cheese> getCheeses() {
		try (Connection connection = sql2o.open()) {
			return connection.createQuery("SELECT name, price from cheeses")
					.executeAndFetch(Cheese.class);
		}
	}

	@Override
	protected void init() {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		final IResourceFinder defaultResourceFinder = getResourceFinder();

		// retrieve resources from the /resource directory when deployed to Tomcat
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
	}

	@Override
	public Session newSession(Request request, Response response) {
		return new CheesrSession(request);
	}
}
