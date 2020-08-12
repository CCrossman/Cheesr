package com.crossman;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.file.IResourceFinder;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.UrlResourceStream;

import java.math.BigDecimal;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class WicketApplication extends WebApplication {
	@Getter
	private final List<Cheese> cheeses = Collections.unmodifiableList(Arrays.asList(
			new Cheese("Gouda", BigDecimal.valueOf(1.65)),
			new Cheese("Edam", BigDecimal.valueOf(1.05)),
			new Cheese("Maasdam", BigDecimal.valueOf(2.35)),
			new Cheese("Brie", BigDecimal.valueOf(3.15)),
			new Cheese("Buxton Blue", BigDecimal.valueOf(0.99)),
			new Cheese("Parmesan", BigDecimal.valueOf(1.99)),
			new Cheese("Cheddar", BigDecimal.valueOf(2.95)),
			new Cheese("Roquefort", BigDecimal.valueOf(1.67)),
			new Cheese("Boursin", BigDecimal.valueOf(1.33)),
			new Cheese("Camembert", BigDecimal.valueOf(1.69)),
			new Cheese("Emmental", BigDecimal.valueOf(2.39)),
			new Cheese("Reblochon", BigDecimal.valueOf(2.99))
	));

	public static WicketApplication get() {
		return (WicketApplication) Application.get();
	}

	public Class<?> getHomePage() {
		return HomePage.class;
	}

	@Override
	protected void init() {
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
