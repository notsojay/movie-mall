package com.functional;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetGetter<T> {
    T get(ResultSet dbResultSet, String columnName) throws SQLException;
}