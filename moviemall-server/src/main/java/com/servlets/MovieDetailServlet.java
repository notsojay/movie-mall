package com.servlets;

import com.model.MovieEntity;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import java.sql.Connection;

import static com.adapter.MovieAdapter.*;
import static com.utils.DatabaseManager.*;
import static com.utils.URLUtils.*;

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
                    SELECT GROUP_CONCAT(DISTINCT CONCAT(s.name, '|', s.id) ORDER BY s.name ASC)
                    FROM stars_in_movies sm
                    LEFT JOIN stars s ON sm.starId = s.id
                    WHERE sm.movieId = m.id
                ) AS star_name_id_pairs,
                (
                    SELECT r.rating
                    FROM ratings r
                    WHERE r.movieId = m.id
                ) AS rating
            FROM movies m
            WHERE m.id = ?
            """;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try (Connection conn = getJNDIDatabaseConnection()) {

            String movie_id = request.getParameter("query");
            if (movie_id == null) throw new ServletException("ERROR: Invalid URL");
            movie_id = decodeFromBase64(movie_id);

            JSONObject result = queryFrom_moviedb(
                    conn,
                    SQL_QUERY,
                    rs -> {
                        if (!rs.next()) return null;
                        MovieEntity movie = extractMovieFromDbResultSet(rs);
                        return convertMovieEntityToJson(movie);
                    },
                    movie_id
            );

            if(result == null) throw new ServletException("ERROR: Movie not found");
            super.sendJsonDataResponse(response, result);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }
}
