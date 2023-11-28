package com.servlets;

import com.adapter.CustomerAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.models.UserEntity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;

import static com.adapter.CustomerAdapter.convertAuthResponseToJson;
import static com.adapter.CustomerAdapter.extractPasswordFromDbResultSet;
import static com.db.DatabaseManager.getJNDIDatabaseConnection;
import static com.db.DatabaseManager.execDbQuery;
import static com.utils.ReCaptchaService.verifyRecaptcha;

@WebServlet("/AuthenticationServlet")
public class AuthenticationServlet extends AbstractServletBase {
    private static final String SQL_QUERY_CUSTOMER = """
            SELECT password
            FROM customers
            WHERE email = ?;
            """;

    private static final String SQL_QUERY_EMPLOYEE = """
            SELECT password
            FROM employees
            WHERE email = ?;
            """;

    public enum AuthResult {
        SUCCESS,
        EMAIL_NOT_FOUND,
        PASSWORD_INCORRECT,
        IS_EMPTY,
        RECAPTCHA_FAILED
    }

    @Override
    public void init() {
        StrongPasswordEncryptor encryptor = new StrongPasswordEncryptor();
        getServletContext().setAttribute("encryptor", encryptor);
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

        super.sendJsonDataResponse(response, HttpServletResponse.SC_OK, jsonResponse);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserEntity credentials = objectMapper.readValue(request.getReader(), UserEntity.class);
            String userType = credentials.getUserType();
            String email = credentials.getEmail();
            String password = credentials.getPassword();
            boolean useRECAPTCHA = credentials.isUseRECAPTCHA();
            String captchaValue = credentials.getCaptchaValue();

            AuthResult authResult = authenticate(request, userType, email, password, captchaValue, useRECAPTCHA);

            JSONObject jsonResponse = switch (authResult) {
                case SUCCESS -> convertAuthResponseToJson("success", "Logged in successfully");
                case PASSWORD_INCORRECT -> convertAuthResponseToJson("error", "Incorrect password");
                case EMAIL_NOT_FOUND -> convertAuthResponseToJson("error", "Username not found");
                case IS_EMPTY -> convertAuthResponseToJson("error", "Empty content");
                case RECAPTCHA_FAILED -> convertAuthResponseToJson("error", "Recaptcha verification failed");
            };

            super.sendJsonDataResponse(response, HttpServletResponse.SC_OK, jsonResponse);

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

            JSONObject jsonResponse = CustomerAdapter.convertAuthResponseToJson("success", "Logged out successfully");
            super.sendJsonDataResponse(response, HttpServletResponse.SC_OK, jsonResponse);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }

    private AuthResult authenticate(HttpServletRequest request, String userType, String email, String password, String captchaValue, boolean useRECAPTCHA) throws Exception {
        try (Connection conn = getJNDIDatabaseConnection()) {

            if (!verifyRecaptcha(captchaValue) && useRECAPTCHA) {
                return AuthResult.RECAPTCHA_FAILED;
            }
            if (email == null || password == null) {
                return AuthResult.IS_EMPTY;
            }

            String sqlQuery = userType.equals("employee") ? SQL_QUERY_EMPLOYEE : SQL_QUERY_CUSTOMER;

            return execDbQuery(
                    conn,
                    sqlQuery,
                    rs -> {
                        if (!rs.next()) return AuthResult.EMAIL_NOT_FOUND;
                        String encryptedPwd = extractPasswordFromDbResultSet(rs);
                        StrongPasswordEncryptor encryptor = (StrongPasswordEncryptor) getServletContext().getAttribute("encryptor");
                        if (encryptor.checkPassword(password, encryptedPwd)) {
                            HttpSession session = request.getSession(true);
                            session.setAttribute("userEmail", email);
                            session.setAttribute("isLoggedIn", true);
                            session.setMaxInactiveInterval(60 * 30);
                            return AuthResult.SUCCESS;
                        }
                        return AuthResult.PASSWORD_INCORRECT;
                    },
                    email
            );
        }
    }
}
