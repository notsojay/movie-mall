package com.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.models.MovieEntity;
import com.models.StarEntity;
import com.utils.LogUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.logging.Logger;

import static com.adapter.MovieAdapter.convertMovieDtoToJson;
import static com.adapter.MovieAdapter.extractMovieFromDbResultSet;
import static com.db.DatabaseManager.*;
import static com.utils.URLUtils.decodeFromBase64;

@WebServlet("/MovieDetailServlet")
public class MovieDetailServlet extends AbstractServletBase {

    private final Logger performanceLogger = LogUtil.getLogger();

    private static final String SQL_QUERY = """
            SELECT
                   m.title,
                   m.year,
                   m.director,
                   m.price,
                   (
                       SELECT GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC)
                       FROM genres_in_movies gm
                       LEFT JOIN genres g ON gm.genreId = g.id
                       WHERE gm.movieId = m.id
                   ) AS genres,
                   (
                       SELECT GROUP_CONCAT(DISTINCT CONCAT(s.name, '|', s.id) ORDER BY star_movie_count DESC, s.name ASC)
                       FROM stars_in_movies sim
                       LEFT JOIN stars s ON sim.starId = s.id
                       LEFT JOIN (
                           SELECT smc.starId, COUNT(smc.movieId) AS star_movie_count
                           FROM stars_in_movies smc
                           GROUP BY smc.starId
                       ) AS star_counts ON s.id = star_counts.starId
                       WHERE sim.movieId = m.id
                   ) AS star_name_id_pairs,
                   (
                       SELECT r.rating
                       FROM ratings r
                       WHERE r.movieId = m.id
                       LIMIT 1
                   ) AS rating
            FROM movies m
            WHERE m.id = ?;
            """;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try (Connection conn = getJNDIDatabaseConnection(true)) {

            String requestUri = request.getRequestURI();
            String httpMethod = request.getMethod();
            String movie_id = request.getParameter("query");
            if (movie_id == null) {
                throw new ServletException("ERROR: Invalid URL");
            }
            movie_id = decodeFromBase64(movie_id);

            long startJdbcTime = System.nanoTime();
            JSONObject finalResult = execDbQuery(
                    conn,
                    SQL_QUERY,
                    rs -> {
                        if (!rs.next()) return null;
                        MovieEntity movie = extractMovieFromDbResultSet(rs);
                        return convertMovieDtoToJson(movie);
                    },
                    movie_id
            );
            long endJdbcTime = System.nanoTime();
            long jdbcTime = endJdbcTime - startJdbcTime;
            performanceLogger.info(httpMethod + " " + requestUri + " - JDBC Time: " + jdbcTime + "ns");

            if (finalResult == null) throw new ServletException("ERROR: Movie not found");
            super.sendJsonDataResponse(response, HttpServletResponse.SC_OK, finalResult);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        try (Connection conn = getJNDIDatabaseConnection(false)) {

            String requestUri = request.getRequestURI();
            String httpMethod = request.getMethod();
            ObjectMapper objectMapper = new ObjectMapper();
            MovieEntity movie = objectMapper.readValue(request.getReader(), MovieEntity.class);
            String movieTitle = movie.getTitle();
            Integer releaseYear = movie.getYear();
            String director = movie.getDirector();
            String leadStarName = movie.getLeadStarName();
            Integer leadStarBirthYear = movie.getLeadStarBirthYear();
            String movieGenre = movie.getMainGenre();

            long startJdbcTime = System.nanoTime();
            JSONObject finalResult = execDbProcedure(
                    conn,
                    "{CALL add_movie(?, ?, ?, ?, ?, ?, ?, ?, ?)}",
                    (stmt, hadResults) -> {
                        if (hadResults) {
                            String movieId = stmt.getString(7);
                            String starId = stmt.getString(8);
                            Integer genreId = stmt.getInt(9);

                            if (movieId != null && starId != null && genreId != 0) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("movie_id", movieId);
                                jsonObject.put("star_id", starId);
                                jsonObject.put("genre_id", genreId);
                                return jsonObject;
                            }
                        }
                        return null;
                    },
                    new Object[]{movieTitle, releaseYear, director, leadStarName, leadStarBirthYear, movieGenre, null, null, null},
                    new int[]{Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.INTEGER},
                    new boolean[]{false, false, false, false, false, false, true, true, true}
            );
            long endJdbcTime = System.nanoTime();
            long jdbcTime = endJdbcTime - startJdbcTime;
            performanceLogger.info(httpMethod + " " + requestUri + " - JDBC Time: " + jdbcTime + "ns");

            if (finalResult == null) {
                super.sendStatusResponse(response, HttpServletResponse.SC_BAD_REQUEST, "ERROR: Adding movie");
                return;
            }

            super.sendJsonDataResponse(response, HttpServletResponse.SC_OK, finalResult);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }
}
