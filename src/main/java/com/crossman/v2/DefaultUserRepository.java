package com.crossman.v2;

import org.simpleflatmapper.sql2o.SfmResultSetHandlerFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Query;
import org.sql2o.ResultSetHandler;
import org.sql2o.Sql2o;

import javax.inject.Inject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Optional;

public final class DefaultUserRepository implements UserRepository {
	private static final Logger logger = LoggerFactory.getLogger(DefaultUserRepository.class);
	private static final String QUERY = "SELECT u.id as id, u.username as username, u.created as created, p.active as active, p.admin as admin FROM users u JOIN permissions p ON u.id = p.user_id WHERE username = :name ";
	private static final SfmResultSetHandlerFactoryBuilder RESULT_SET_HANDLER_FACTORY_BUILDER = new SfmResultSetHandlerFactoryBuilder();

	@Inject
	private Sql2o sql2o;

	@Override
	public Optional<StoredUser> findByName(String name) {
		try (Connection c = sql2o.open()) {
			Query query = c.createQuery(QUERY);
			query.setAutoDeriveColumnNames(true);
			query.setResultSetHandlerFactoryBuilder(RESULT_SET_HANDLER_FACTORY_BUILDER);
			query.addParameter("name", name);

			List<StoredUser> storedUsers = query.executeAndFetch(StoredUserResultSetHandler.instance);
			return storedUsers.isEmpty() ? Optional.empty() : Optional.of(storedUsers.get(0));
		} catch (Exception e) {
			logger.warn("Problem finding user in repository", e);
			return Optional.empty();
		}
	}

	private static enum StoredUserResultSetHandler implements ResultSetHandler<StoredUser> {
		instance;

		private final ZoneId zoneId = ZoneId.systemDefault();
		private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

		@Override
		public StoredUser handle(ResultSet resultSet) throws SQLException {
			long id = resultSet.getLong("id");
			String name = resultSet.getString("username");
			boolean active = resultSet.getBoolean("active");
			boolean admin = resultSet.getBoolean("admin");

			final String raw = resultSet.getString("created");
			final TemporalAccessor parsed = formatter.parse(raw);
			final ZonedDateTime created = LocalDateTime.from(parsed).atZone(zoneId);
			return new StoredUser(id, name, active, admin, created);
		}
	}
}
