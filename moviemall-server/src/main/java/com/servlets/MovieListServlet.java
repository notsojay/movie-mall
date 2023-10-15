package com.servlets;

import com.model.MovieEntity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;

import static com.adapter.MovieAdapter.*;
import static com.utils.DatabaseManager.*;

@WebServlet("/MovieListServlet")
public class MovieListServlet extends AbstractServletBase {

    private static final String SQL_QUERY = """
            SELECT
                movie_id,
                m.title,
                m.year,
                m.director,
                subquery.rating,
                (
                    SELECT GROUP_CONCAT(DISTINCT limited_genres.name ORDER BY limited_genres.name ASC)
                    FROM (
                        SELECT g.name
                        FROM genres_in_movies gm
                        LEFT JOIN genres g ON gm.genreId = g.id
                        WHERE gm.movieId = m.id
                        LIMIT 3
                    ) AS limited_genres
                ) AS genres,
                (
                    SELECT GROUP_CONCAT(DISTINCT CONCAT(limited_stars.name, '|', limited_stars.id) ORDER BY limited_stars.name ASC)
                    FROM (
                        SELECT s.name, s.id
                        FROM stars_in_movies sm
                        LEFT JOIN stars s ON sm.starId = s.id
                        WHERE sm.movieId = m.id
                        LIMIT 3
                    ) AS limited_stars
                ) AS star_name_id_pairs
            FROM (
                SELECT
                    m.id AS movie_id,
                    r.rating
                FROM movies m
                LEFT JOIN ratings r ON m.id = r.movieId
                ORDER BY r.rating DESC, m.title ASC
                LIMIT 20
            ) AS subquery
            LEFT JOIN movies m ON m.id = subquery.movie_id
            ORDER BY subquery.rating DESC, m.title ASC;              
            """;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try (Connection conn = getJNDIDatabaseConnection()) {

            JSONArray result = queryFrom_moviedb(
                    conn,
                    SQL_QUERY,
                    rs -> {
                        JSONArray jsonArr = new JSONArray();
                        while (rs.next()) {
                            MovieEntity movie = extractMovieFromDbResultSet(rs);
                            JSONObject jsonObject = convertMovieEntityToJson(movie);
                            jsonArr.put(jsonObject);
                        }
                        return jsonArr;
                    }
            );

            super.sendJsonDataResponse(response, result);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }
}
