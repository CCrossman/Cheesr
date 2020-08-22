package com.crossman.liquibase;

import com.crossman.WicketApplicationModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import org.sql2o.Connection;
import org.sql2o.ResultSetIterable;
import org.sql2o.Sql2o;

public final class AddExistingUsersToPermissions implements CustomTaskChange {
	private static final Injector injector = Guice.createInjector(new WicketApplicationModule());

	private ResourceAccessor resourceAccessor;

	@Override
	public void execute(Database database) throws CustomChangeException {
		final Sql2o sql2o = injector.getInstance(Sql2o.class);

		try (Connection c = sql2o.open()) {
			try (ResultSetIterable<Long> userIds = c.createQuery("SELECT id from users").executeAndFetchLazy(Long.class)) {
				for (Long userId : userIds) {
					c.createQuery("INSERT INTO permissions (user_id, active, admin) VALUES (:id, true, false)")
						.addParameter("id", userId)
						.executeUpdate();
				}
			}
		}
	}

	@Override
	public String getConfirmationMessage() {
		return "Ran AddExistingUsersToPermissions custom change";
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
}
