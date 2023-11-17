package com.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static com.db.DatabaseManager.*;

public class UpdateSecurePassword {
    private static final String MODIFY_CUSTOMER_PASSWORD_COLUMN_QUERY = "ALTER TABLE customers MODIFY COLUMN password VARCHAR(128)";
    private static final String SELECT_ALL_CUSTOMER_QUERY = "SELECT id, password from customers";
    private static final String UPDATE_CUSTOMER_PWD_QUERY = "UPDATE customers SET password='%s' WHERE id=%s;";
    private static final String MODIFY_EMPLOYEE_PASSWORD_COLUMN_QUERY = "ALTER TABLE employees MODIFY COLUMN password VARCHAR(128)";
    private static final String SELECT_ALL_EMPLOYEE_QUERY = "SELECT email, password from employees";
    private static final String UPDATE_EMPLOYEE_PWD_QUERY = "UPDATE employees SET password='%s' WHERE email='%s';";

    public static void execUpdateDbAllCustomerPwdToSecure(Connection conn) throws SQLException, JsonProcessingException {
        execDbUpdate(
                conn,
                MODIFY_CUSTOMER_PASSWORD_COLUMN_QUERY,
                alterResult -> System.out.println("altering customers table schema completed, " + alterResult + " rows affected")
        );

        ArrayList<String> updateQueryList = execDbQuery(
                conn,
                SELECT_ALL_CUSTOMER_QUERY,
                rs -> {
                    PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
                    ArrayList<String> innerUpdateQueryList = new ArrayList<>();
                    System.out.println("encrypting password (this might take a while)");

                    while (rs.next()) {
                        String id = rs.getString("id");
                        String password = rs.getString("password");
                        String encryptedPassword = passwordEncryptor.encryptPassword(password);
                        String updateQuery = String.format(UPDATE_CUSTOMER_PWD_QUERY, encryptedPassword, id);
                        innerUpdateQueryList.add(updateQuery);
                    }

                    System.out.println("updating password");
                    return innerUpdateQueryList;
                }
        );

        AtomicInteger count = new AtomicInteger();
        for (String updateQuery : updateQueryList) {
            execDbUpdate(conn, updateQuery, count::addAndGet);
        }
        System.out.println("updating password completed, " + count + " rows affected");
        System.out.println("finished");
    }

    public static void execUpdateDbAllEmployeePwdToSecure(Connection conn) throws SQLException, JsonProcessingException {
        execDbUpdate(
                conn,
                MODIFY_EMPLOYEE_PASSWORD_COLUMN_QUERY,
                alterResult -> System.out.println("altering employees table schema completed, " + alterResult + " rows affected")
        );

        ArrayList<String> updateQueryList = execDbQuery(
                conn,
                SELECT_ALL_EMPLOYEE_QUERY,
                rs -> {
                    PasswordEncryptor passwordEncryptor = new StrongPasswordEncryptor();
                    ArrayList<String> innerUpdateQueryList = new ArrayList<>();
                    System.out.println("encrypting password (this might take a while)");

                    while (rs.next()) {
                        String email = rs.getString("email");
                        String password = rs.getString("password");
                        String encryptedPassword = passwordEncryptor.encryptPassword(password);
                        String updateQuery = String.format(UPDATE_EMPLOYEE_PWD_QUERY, encryptedPassword, email);
                        innerUpdateQueryList.add(updateQuery);
                    }

                    System.out.println("updating password");
                    return innerUpdateQueryList;
                }
        );

        AtomicInteger count = new AtomicInteger();
        for (String updateQuery : updateQueryList) {
            execDbUpdate(conn, updateQuery, count::addAndGet);
        }
        System.out.println("updating password completed, " + count + " rows affected");
        System.out.println("finished");
    }

    public static void main(String[] args) throws Exception {
        try (Connection conn = getDirectDatabaseConnection(
                "jdbc:mysql://localhost:3306/moviedb",
                "cs122b",
                "pwd_is_team_NO_1"))
        {
            execUpdateDbAllCustomerPwdToSecure(conn);
            execUpdateDbAllEmployeePwdToSecure(conn);
        }
    }
}