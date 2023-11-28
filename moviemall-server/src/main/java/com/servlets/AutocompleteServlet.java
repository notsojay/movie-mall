package com.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.db.DatabaseManager.execDbQuery;
import static com.db.DatabaseManager.getJNDIDatabaseConnection;
import static com.utils.URLUtils.encodeToBase64;

@WebServlet("/AutocompleteServlet")
public class AutocompleteServlet extends AbstractServletBase {

    private static final Logger logger = Logger.getLogger(MovieListServlet.class.getName());

    private static final String SQL_QUERY = """
                SELECT m.title AS title, MIN(m.id) AS movie_id
                FROM movies m
                WHERE m.title LIKE ?
                GROUP BY m.title
                ORDER BY LOCATE(?, m.title), m.title
                LIMIT 10
            """;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");

        if (query == null || query.length() < 3) {
            super.sendJsonDataResponse(response, HttpServletResponse.SC_OK, new JSONArray());
            return;
        }

        try (Connection conn = getJNDIDatabaseConnection()) {
            JSONArray finalResult = execDbQuery(
                    conn,
                    SQL_QUERY,
                    rs -> {
                        JSONArray suggestions = new JSONArray();
                        while (rs.next()) {
                            JSONObject jsonObject = new JSONObject();
                            String title = rs.getString("title");
                            String movieId = rs.getString("movie_id");
                            movieId = encodeToBase64(movieId);
                            jsonObject.put("title", title);
                            jsonObject.put("id", movieId);
                            suggestions.put(jsonObject);
                        }
                        return suggestions;
                    },
                    "%" + query + "%",
                    query
            );

            super.sendJsonDataResponse(response, HttpServletResponse.SC_OK, finalResult);

        } catch (Exception e) {
            throw new ServletException("AutocompleteServlet SQL error", e);
        }
    }
}