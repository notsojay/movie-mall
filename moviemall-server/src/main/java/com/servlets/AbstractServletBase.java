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

    protected <T> void sendJsonDataResponse(HttpServletResponse response, T json) throws IOException {
        if (!(json instanceof JSONObject) && !(json instanceof JSONArray)) {
            throw new IllegalArgumentException("ERROR: Provided object is neither a JSONObject nor a JSONArray");
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.print(json);
            out.flush();
        }
    }
}