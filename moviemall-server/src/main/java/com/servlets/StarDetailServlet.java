package com.servlets;

import com.models.StarEntity;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.sql.Connection;

import static com.adapter.StarAdapter.*;
import static com.utils.DatabaseManager.*;
import static com.utils.URLUtils.decodeFromBase64;

@WebServlet("/StarDetailServlet")
public class StarDetailServlet extends AbstractServletBase {

    private static final String SQL_QUERY = """
            SELECT
                s.name AS star_name,
                s.birthYear AS star_birth_year,
                (
                    SELECT GROUP_CONCAT(DISTINCT CONCAT(m.title, '|', m.id, '|', m.director, '|', m.year) ORDER BY m.year DESC, m.title ASC)
                    FROM stars_in_movies sm
                    LEFT JOIN movies m ON sm.movieId = m.id
                    WHERE sm.starId = s.id AND m.director IS NOT NULL AND m.year IS NOT NULL
                ) AS movie_infos
            FROM stars s
            WHERE s.id = ?
        """;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try (Connection conn = getJNDIDatabaseConnection()) {

            String star_id = request.getParameter("query");
            if (star_id == null) {
                throw new ServletException("ERROR: Invalid URL");
            }
            star_id = decodeFromBase64(star_id);

            JSONObject finalResult = queryFrom_moviedb(
                    conn,
                    SQL_QUERY,
                    rs -> {
                        if (!rs.next()) return null;
                        StarEntity star = extractStarFromDbResultSet(rs);
                        return convertStarEntityToJson(star);
                    },
                    star_id
            );

            if (finalResult == null) throw new ServletException("ERROR: Star not found");
            super.sendJsonDataResponse(response, finalResult);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }
}
