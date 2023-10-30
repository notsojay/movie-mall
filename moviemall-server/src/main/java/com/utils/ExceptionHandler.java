package com.utils;

import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.sql.SQLException;
import javax.naming.NamingException;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;

public class ExceptionHandler {

    private static final Logger LOGGER = Logger.getLogger(ExceptionHandler.class.getName());

    private static final Map<Class<? extends Exception>, ErrorResponse> EXCEPTION_MAPPINGS;

    static {
        EXCEPTION_MAPPINGS = new HashMap<>();
        EXCEPTION_MAPPINGS.put(SQLException.class, new ErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error"));
        EXCEPTION_MAPPINGS.put(NamingException.class, new ErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Configuration error"));
        EXCEPTION_MAPPINGS.put(JsonProcessingException.class, new ErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error"));
        EXCEPTION_MAPPINGS.put(IllegalArgumentException.class, new ErrorResponse(HttpServletResponse.SC_BAD_REQUEST, "Invalid request"));
        EXCEPTION_MAPPINGS.put(IOException.class, new ErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server communication error"));
        EXCEPTION_MAPPINGS.put(ServletException.class, new ErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Servlet error"));
        EXCEPTION_MAPPINGS.put(SQLSyntaxErrorException.class, new ErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "SQL syntax error"));
        EXCEPTION_MAPPINGS.put(java.lang.NullPointerException.class, new ErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "NULL pointer error"));
    }

//    public void handleException(HttpServletResponse response, Exception e) {
//        LOGGER.log(Level.SEVERE, "Error: " + e.getClass().getName() + " - " + e.getMessage(), e);
//        ErrorResponse errorResponse = EXCEPTION_MAPPINGS.getOrDefault(e.getClass(), new ErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unknown server error"));
//        errorResponse.send(response);
//    }
    public void handleException(HttpServletResponse response, Exception e) {
        LOGGER.log(Level.SEVERE, "Error: " + e.getClass().getName() + " - " + e.getMessage(), e);
        if(EXCEPTION_MAPPINGS.containsKey(e.getClass())) {
            ErrorResponse errorResponse = EXCEPTION_MAPPINGS.get(e.getClass());
            errorResponse.send(response);
        } else {
            LOGGER.log(Level.SEVERE, "No mapping found for exception: " + e.getClass().getName());
            new ErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unknown server error").send(response);
        }
    }

    private static class ErrorResponse {
        int statusCode;
        String message;

        ErrorResponse(int statusCode, String message) {
            this.statusCode = statusCode;
            this.message = message;
        }

        public void send(HttpServletResponse response) {
            try {
                response.setStatus(statusCode);
                response.getWriter().write("{\"ERROR\": \"" + message + "\"}");
            } catch (IOException ioException) {
                LOGGER.log(Level.SEVERE, "Error writing error response", ioException);
            }
        }
    }
}
