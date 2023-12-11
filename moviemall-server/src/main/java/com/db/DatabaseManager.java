package com.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.functional.ProcedureResultProcessor;
import com.functional.QueryResultProcessor;
import com.functional.ResultSetGetter;
import com.functional.UpdateCountProcessor;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.logging.Logger;

public class DatabaseManager {

//    private static final Logger logger = Logger.getLogger(DatabaseManager.class.getName());

    private DatabaseManager() {
        // Prevent Instantiation
    }

    public static Connection getDirectDatabaseConnection(String jdbcUrl, String username, String password) throws SQLException {
        Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
        System.out.println(jdbcUrl + " connection successful!");
        return conn;
    }

//    public static Connection getJNDIDatabaseConnection() throws NamingException, SQLException {
//        Context ctx = new InitialContext();
//        DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/moviedb");
//        return ds.getConnection();
//    }

    public static Connection getJNDIDatabaseConnection(boolean isReadOperation) throws NamingException, SQLException {
        Context ctx = new InitialContext();
        DataSource ds;
        if (isReadOperation) {
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/moviedbSlave");
        } else {
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/moviedbMaster");
        }
        return ds.getConnection();
    }

    public static <T> T execDbQuery(Connection conn, String sql, QueryResultProcessor<T> processor, Object... params) throws SQLException, JsonProcessingException {
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

    public static void execDbUpdate(Connection conn, String sql, UpdateCountProcessor processor, Object... params) throws SQLException {
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

    public static <T> T execDbProcedure(Connection conn, String procedureCall, ProcedureResultProcessor<T> processor, Object[] params, int[] sqlTypes, boolean[] isOutParam) throws SQLException {
        if (conn == null) {
            throw new IllegalArgumentException("Error: Connection is null");
        }
        if (procedureCall == null || procedureCall.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Procedure call is null or empty");
        }

        try (CallableStatement stmt = conn.prepareCall(procedureCall)) {
            for (int i = 0; i < params.length; ++i) {
                if (isOutParam[i]) {
                    stmt.registerOutParameter(i + 1, sqlTypes[i]);
                } else {
                    if (params[i] != null) {
                        stmt.setObject(i + 1, params[i]);
                    } else {
                        stmt.setNull(i + 1, sqlTypes[i]);
                    }
                }
            }

            boolean hadResults = stmt.execute();
            return processor.process(stmt, hadResults);
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
