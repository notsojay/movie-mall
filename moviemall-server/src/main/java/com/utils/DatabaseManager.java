package com.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.functional.QueryResultProcessor;
import com.functional.ResultSetGetter;
import com.functional.UpdateResultProcessor;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;

public class DatabaseManager {

    private DatabaseManager() {
        // Prevent Instantiation
    }

    public static Connection getJDBCDatabaseConnection(String jdbcUrl, String username, String password) throws SQLException {
        Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
        System.out.println(jdbcUrl + " connection successful!");
        return conn;
    }

    public static Connection getJNDIDatabaseConnection() throws NamingException, SQLException {
        Context ctx = new InitialContext();
        DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/moviedb");
        return ds.getConnection();
    }

    public static <T> T queryFrom_moviedb(Connection conn, String sql, QueryResultProcessor<T> processor, Object... params) throws SQLException, JsonProcessingException {
        if (conn == null) {
            throw new IllegalArgumentException("Error: Connection is null");
        }
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: SQL query is null or empty");
        }

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; ++i) {
                statement.setObject(i+1, params[i]);
            }
            try (ResultSet rs = statement.executeQuery()) {
                return processor.process(rs);
            }
        }
    }

    public static void updateIn_moviedb(Connection conn, String sql, UpdateResultProcessor processor, Object... params) throws SQLException {
        if (conn == null) {
            throw new IllegalArgumentException("Error: Connection is null");
        }
        if (sql == null || sql.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: SQL query is null or empty");
        }

        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; ++i) {
                statement.setObject(i+1, params[i]);
            }
            int updateCount = statement.executeUpdate();
            processor.process(updateCount);
        }
    }

    public static <T> T getSafeColumnValue(ResultSet rs, String columnName, ResultSetGetter<T> getter) {
        try {
            return getter.get(rs, columnName);
        } catch (SQLException e) {
            return null;
        }
    }
}
