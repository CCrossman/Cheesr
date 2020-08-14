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

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.Properties;

@NoArgsConstructor
public class WicketApplication extends WebApplication {
	private Sql2o sql2o;

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
		// load properties from application.properties file
		Properties properties = new Properties();
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties")) {
			properties.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// initialize database connection proxy
		this.sql2o = new Sql2o(properties.getProperty("POSTGRES_URL"), System.getenv("POSTGRES_USERNAME"), System.getenv("POSTGRES_PASSWORD"));

		// initialize database driver
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
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
	}

	@Override
	public Session newSession(Request request, Response response) {
		return new CheesrSession(request);
	}
}
