package com.crossman;

import com.crossman.v1.Address;
import com.crossman.v1.Cheese;
import com.crossman.v1.DefaultAddressShower;
import com.crossman.v1.DefaultCheeseListShower;
import com.crossman.v2.CheesrProduct;
import com.crossman.v2.DefaultCheesrProductListShower;
import com.crossman.v2.DefaultUserRepository;
import com.crossman.v2.UserRepository;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Sql2o;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public final class WicketApplicationModule extends AbstractModule {
	private static final Logger logger = LoggerFactory.getLogger(WicketApplicationModule.class);

	@Override
	protected void configure() {
		super.configure();

		// load properties from application.properties file
		final Properties properties = new Properties();
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("application.properties")) {
			properties.load(is);
		} catch (IOException e) {
			logger.error("Problem loading application.properties file", e);
			throw new RuntimeException(e);
		}
		bind(Properties.class).toInstance(properties);

		// initialize database driver
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			logger.error("Problem loading org.postgresql.Driver", e);
			throw new RuntimeException(e);
		}

		// initialize the database connection proxy
		final Sql2o sql2o = new Sql2o(properties.getProperty("POSTGRES_URL"), System.getenv("POSTGRES_USERNAME"), System.getenv("POSTGRES_PASSWORD"));
		bind(Sql2o.class).toInstance(sql2o);

		// initialize the encryptor
		final DefaultEncryptor de = new DefaultEncryptor(properties.getProperty("ENC_SECRET"), properties.getProperty("ENC_SALT"));
		de.init();
		bind(Encryptor.class).toInstance(de);

		// bind the showers
		bind(new Key<Show<Address>>() {}).toInstance(DefaultAddressShower.instance);
		bind(new Key<Show<List<Cheese>>>() {}).toInstance(DefaultCheeseListShower.instance);
		bind(new Key<Show<List<CheesrProduct>>>() {}).toInstance(DefaultCheesrProductListShower.instance);

		// bind repositories
		bind(UserRepository.class).to(DefaultUserRepository.class);
	}
}
