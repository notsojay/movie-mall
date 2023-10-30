package com.servlets;

import com.adapter.CustomerAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.models.CustomerEntity;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

import javax.naming.NamingException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static com.adapter.CustomerAdapter.*;
import static com.utils.DatabaseManager.getJNDIDatabaseConnection;
import static com.utils.DatabaseManager.queryFrom_moviedb;

@WebServlet("/AuthenticationServlet")
public class AuthenticationServlet extends AbstractServletBase {
    private static final String SQL_QUERY = """
            SELECT password
            FROM customers
            WHERE email = ?;
            """;

    public enum AuthResult {
        SUCCESS,
        EMAIL_NOT_FOUND,
        PASSWORD_INCORRECT
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);

        JSONObject jsonResponse = new JSONObject();

        if (session != null && session.getAttribute("isLoggedIn") != null && (boolean) session.getAttribute("isLoggedIn")) {
            jsonResponse.put("status", "logged-in");
            jsonResponse.put("email", session.getAttribute("userEmail").toString());
        } else {
            jsonResponse.put("status", "not-logged-in");
        }

        super.sendJsonDataResponse(response, jsonResponse);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CustomerEntity credentials = objectMapper.readValue(request.getReader(), CustomerEntity.class);
            String email = credentials.getEmail();
            String password = credentials.getPassword();

            if (email == null || password == null) {
                throw new ServletException("ERROR: Empty Username/Password");
            }

            AuthResult authResult = authenticate(email, password, request);

            JSONObject jsonResponse = switch (authResult) {
                case SUCCESS -> convertStatusResponseToJson("success", "Logged in successfully");
                case PASSWORD_INCORRECT -> convertStatusResponseToJson("error", "Incorrect password");
                case EMAIL_NOT_FOUND -> convertStatusResponseToJson("error", "Username not found");
            };

            super.sendJsonDataResponse(response, jsonResponse);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)  {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            JSONObject jsonResponse = CustomerAdapter.convertStatusResponseToJson("success", "Logged out successfully");
            super.sendJsonDataResponse(response, jsonResponse);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }

    private AuthResult authenticate(String email, String password, HttpServletRequest request) throws SQLException, NamingException, JsonProcessingException {
        try (Connection conn = getJNDIDatabaseConnection()) {

            String storedPassword = queryFrom_moviedb(
                    conn,
                    SQL_QUERY,
                    rs -> {
                        if (!rs.next()) return null;
                        return extractPasswordFromDbResultSet(rs);
                    },
                    email
            );

            if (storedPassword == null) {
                return AuthResult.EMAIL_NOT_FOUND;
            } else if (!storedPassword.equals(password)) {
                return AuthResult.PASSWORD_INCORRECT;
            } else {
                HttpSession session = request.getSession(true);
                session.setAttribute("userEmail", email);
                session.setAttribute("isLoggedIn", true);
                session.setMaxInactiveInterval(60 * 30);
                return AuthResult.SUCCESS;
            }
        }
    }
}
