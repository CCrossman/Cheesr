package com.crossman.v1;

import org.sql2o.ResultSetHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public enum AddressResultSetHandler implements ResultSetHandler<Address> {
	instance;

	@Override
	public Address handle(ResultSet resultSet) throws SQLException {
		Address.Type kind = Address.Type.valueOf(resultSet.getString("kind"));
		String name = resultSet.getString("name");
		String street = resultSet.getString("street");
		String city = resultSet.getString("city");
		String state = resultSet.getString("state");
		String zip = resultSet.getString("zip");
		return new Address(kind, name, street, city, state, zip);
	}
}
