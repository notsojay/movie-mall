package com.functional;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface QueryResultProcessor<T> {
    T process(ResultSet dbResultSet) throws SQLException, JsonProcessingException;
}
