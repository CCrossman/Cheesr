package com.crossman;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.wicket.Application;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;

import java.math.BigDecimal;
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
	public Session newSession(Request request, Response response) {
		return new CheesrSession(request);
	}
}
