package com.servlets;

import com.models.MovieEntity;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.sql.Connection;

import static com.adapter.MovieAdapter.convertMovieDtoToJson;
import static com.adapter.MovieAdapter.extractMovieFromDbResultSet;
import static com.utils.DatabaseManager.getJNDIDatabaseConnection;
import static com.utils.DatabaseManager.queryFrom_moviedb;
import static com.utils.URLUtils.decodeFromBase64;

@WebServlet("/MovieDetailServlet")
public class MovieDetailServlet extends AbstractServletBase {

    private static final String SQL_QUERY = """
            SELECT
                   m.title,
                   m.year,
                   m.director,
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
                   ) AS rating
            FROM movies m
            WHERE m.id = ?;
            """;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try (Connection conn = getJNDIDatabaseConnection()) {

            String movie_id = request.getParameter("query");
            if (movie_id == null) {
                throw new ServletException("ERROR: Invalid URL");
            }
            movie_id = decodeFromBase64(movie_id);

            JSONObject finalResult = queryFrom_moviedb(
                    conn,
                    SQL_QUERY,
                    rs -> {
                        if (!rs.next()) return null;
                        MovieEntity movie = extractMovieFromDbResultSet(rs);
                        return convertMovieDtoToJson(movie);
                    },
                    movie_id
            );

            if (finalResult == null) throw new ServletException("ERROR: Movie not found");
            super.sendJsonDataResponse(response, finalResult);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }
}
