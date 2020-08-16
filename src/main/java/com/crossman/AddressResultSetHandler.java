package com.crossman;

import org.sql2o.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public enum AddressResultSetHandler implements ResultSetHandler<Address> {
	instance;

	@Override
	public Address handle(ResultSet resultSet) throws SQLException {
		String name = resultSet.getString("name");
		String street = resultSet.getString("street");
		String city = resultSet.getString("city");
		String state = resultSet.getString("state");
		String zip = resultSet.getString("zip");
		return new Address(name, street, city, state, zip);
	}
}
