package com.servlets;

import com.utils.ExceptionHandler;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;

public abstract class AbstractServletBase extends HttpServlet {

    protected final ExceptionHandler exceptionHandler = new ExceptionHandler();

    protected <T> void sendJsonDataResponse(HttpServletResponse response, int status, T json) throws IOException {
        if (!(json instanceof JSONObject) && !(json instanceof JSONArray)) {
            throw new IllegalArgumentException("ERROR: Provided object is neither a JSONObject nor a JSONArray");
        }

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.print(json);
            out.flush();
        }
    }

    protected void sendStatusResponse(HttpServletResponse response, int status, String message) throws IOException {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("ERROR: Invalid String");
        }
        response.setStatus(status);
        response.getWriter().write(message);
    }
}