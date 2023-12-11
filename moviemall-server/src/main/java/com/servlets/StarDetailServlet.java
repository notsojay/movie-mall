package com.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.models.StarEntity;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.Types;
import java.util.logging.Logger;

import static com.adapter.StarAdapter.*;
import static com.db.DatabaseManager.*;
import static com.utils.URLUtils.decodeFromBase64;

@WebServlet("/StarDetailServlet")
public class StarDetailServlet extends AbstractServletBase {

    private static final Logger logger = Logger.getLogger(MovieListServlet.class.getName());

    private static final String SELECT_SQL_QUERY = """
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
        try (Connection conn = getJNDIDatabaseConnection(true)) {

            String star_id = request.getParameter("query");
            if (star_id == null) {
                throw new ServletException("ERROR: Invalid URL");
            }
            star_id = decodeFromBase64(star_id);

            JSONObject finalResult = execDbQuery(
                    conn,
                    SELECT_SQL_QUERY,
                    rs -> {
                        if (!rs.next()) return null;
                        StarEntity star = extractStarFromDbResultSet(rs);
                        return convertStarEntityToJson(star);
                    },
                    star_id
            );

            if (finalResult == null) throw new ServletException("ERROR: Star not found");
            super.sendJsonDataResponse(response, HttpServletResponse.SC_OK, finalResult);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try (Connection conn = getJNDIDatabaseConnection(false)) {
            ObjectMapper objectMapper = new ObjectMapper();
            StarEntity star = objectMapper.readValue(request.getReader(), StarEntity.class);
            String starName = star.getStarName();
            Integer starBirthYear = star.getStarBirthYear();

            String newStarID = execDbProcedure(
                    conn,
                    "{CALL add_star(?, ?, ?)}",
                    (stmt, hadResults) -> stmt.getString(3),
                    new Object[]{starName, starBirthYear, null},
                    new int[]{Types.VARCHAR, Types.INTEGER, Types.VARCHAR},
                    new boolean[]{false, false, true}
            );

            logger.info("newStarID: " + newStarID + "\n");

            if (newStarID == null) {
                super.sendStatusResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "ERROR: Adding star");
                return;
            }

            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("new_star_id", newStarID);
            super.sendJsonDataResponse(response, HttpServletResponse.SC_OK, jsonResponse);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }
}
