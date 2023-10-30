package com.servlets;

import com.enums.MovieRequestType;
import com.models.MovieEntity;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.adapter.MovieAdapter.*;
import static com.utils.DatabaseManager.*;
import static com.utils.OtherUtils.*;

@WebServlet("/MovieListServlet")
public class MovieListServlet extends AbstractServletBase {

    private static final Logger logger = Logger.getLogger(MovieListServlet.class.getName());

    private static class RequestParameters {
        MovieRequestType movieRequestType = null;
        Integer currPage = null;
        Integer recordsPerPage = null;
        String firstSortKey = null;
        String firstSortOrder = null;
        String secondSortKey = null;
        String secondSortOrder = null;
    }

    private static final String SQL_QUERY_PREFIX = """
            SELECT
                m.id AS movie_id,
                m.title,
                m.year,
                m.director,
                subquery.rating,
                m.price,
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
                ) AS star_name_id_pairs,
                COUNT(*) OVER() AS total_records
            FROM movies m
            LEFT JOIN (
                SELECT movieId, rating
                FROM ratings
            ) AS subquery ON m.id = subquery.movieId
         """;

    private static final String SQL_QUERY_SUFFIX = " WHERE subquery.rating IS NOT NULL ";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        try (Connection conn = getJNDIDatabaseConnection()) {

            RequestParameters requestParams = getRequestParameters(request);
            StringBuilder finalQuery = new StringBuilder();
            List<Object> queryParams = new ArrayList<>();

            switch (requestParams.movieRequestType) {
                case GET_TOP20_MOVIES -> buildTop20Query(finalQuery);
                case SEARCH_MOVIES -> buildSearchQuery(request, requestParams, finalQuery, queryParams);
                case BROWSE_MOVIES_BY_GENRE -> buildGenreQuery(request, requestParams, finalQuery, queryParams);
                case BROWSE_MOVIES_BY_INITIAL -> buildInitialQuery(request, requestParams, finalQuery, queryParams);
                case GET_ALL_GENRES -> buildGenresQuery(finalQuery);
                default -> throw new ServletException("ERROR: Unknown type of request");
            }

            JSONArray finalResult = queryFrom_moviedb(
                    conn,
                    finalQuery.toString(),
                    rs -> {
                        JSONArray jsonArr = new JSONArray();
                        while (rs.next()) {
                            MovieEntity movie = extractMovieFromDbResultSet(rs);
                            JSONObject jsonObject = convertMovieDtoToJson(movie);
                            jsonArr.put(jsonObject);
                        }
                        return jsonArr;
                    },
                    queryParams.toArray()
            );

            if (finalResult == null) throw new ServletException("ERROR: Movies not found");
            super.sendJsonDataResponse(response, finalResult);

        } catch (Exception e) {
            super.exceptionHandler.handleException(response, e);
        }
    }

    private RequestParameters getRequestParameters(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        RequestParameters requestParams = null;

        if (session != null) {
            requestParams = (RequestParameters) session.getAttribute("userPreferences");
        }

        if (requestParams == null) {
            requestParams = new RequestParameters();
        }

        requestParams.movieRequestType = getMovieRequestTypeFromRequestOrSessionOrDefault(
                request.getParameter("requestType"),
                requestParams.movieRequestType,
                MovieRequestType.GET_TOP20_MOVIES
        );

        requestParams.currPage = getIntFromRequestOrSessionOrDefault(
                request.getParameter("currentPage"),
                requestParams.currPage,
                1
        );

        requestParams.recordsPerPage = getIntFromRequestOrSessionOrDefault(
                request.getParameter("recordsPerPage"),
                requestParams.recordsPerPage,
                25
        );

        logger.info(request.getParameter("firstSortKey"));
        requestParams.firstSortKey = getStringFromRequestOrSessionOrDefault(
                request.getParameter("firstSortKey"),
                requestParams.firstSortKey,
                "rating"
        );

        requestParams.firstSortOrder = getStringFromRequestOrSessionOrDefault(
                request.getParameter("firstSortOrder"),
                requestParams.firstSortOrder,
                "desc"
        );

        requestParams.secondSortKey = getStringFromRequestOrSessionOrDefault(
                request.getParameter("secondSortKey"),
                requestParams.secondSortKey,
                "title"
        );

        requestParams.secondSortOrder = getStringFromRequestOrSessionOrDefault(
                request.getParameter("secondSortOrder"),
                requestParams.secondSortOrder,
                "asc"
        );

        if (session != null) {
            session.setAttribute("userPreferences", requestParams);
        }

        logger.info("\nfirstSortKey" + requestParams.firstSortKey + "\nfirstsortorder: " + requestParams.firstSortOrder + "\nsecondsortkey: " + requestParams.secondSortKey + " \n : " + requestParams.secondSortOrder);
        return requestParams;
    }

    private void buildTop20Query(StringBuilder finalQuery) {
        finalQuery.append(SQL_QUERY_PREFIX);
        finalQuery.append(
            """
            ORDER BY subquery.rating DESC, m.title ASC
            LIMIT 20
            """
        );

    }

    private void buildSearchQuery(HttpServletRequest request, RequestParameters requestParams, StringBuilder finalQuery, List<Object> queryParams) {
        String title = request.getParameter("title");
        String year = request.getParameter("year");
        String director = request.getParameter("director");
        String starName = request.getParameter("starName");

        finalQuery.append(SQL_QUERY_PREFIX);
        finalQuery.append(SQL_QUERY_SUFFIX);

        if (title != null && !title.isEmpty()) {
            finalQuery.append(" AND m.title LIKE ? ");
            queryParams.add("%" + title + "%");
        }

        if (year != null && !year.isEmpty()) {
            finalQuery.append(" AND m.year = ? ");
            queryParams.add(year);
        }

        if (director != null && !director.isEmpty()) {
            finalQuery.append(" AND m.director LIKE ? ");
            queryParams.add("%" + director + "%");
        }

        if (starName != null && !starName.isEmpty()) {
            finalQuery.append(
               """
                AND EXISTS (
                   SELECT 1
                   FROM stars_in_movies sm
                   LEFT JOIN stars s ON sm.starId = s.id
                   WHERE sm.movieId = m.id
                   AND s.name LIKE ?
               )
               """
            );
            queryParams.add("%" + starName + "%");
        }

        addOrderByClauseToQuery(
                finalQuery,
                requestParams.firstSortKey,
                requestParams.firstSortOrder,
                requestParams.secondSortKey,
                requestParams.secondSortOrder
        );

        addLimitClauseToQuery(finalQuery, queryParams, requestParams.recordsPerPage);
        addOffsetClauseToQuery(finalQuery, queryParams, requestParams.currPage, requestParams.recordsPerPage);
        logger.info("\n" + finalQuery);
        for (int i = 0; i < queryParams.size(); ++i) {
            logger.info("\n" + queryParams.get(i));
        }
    }

    private void buildGenreQuery(HttpServletRequest request, RequestParameters requestParams, StringBuilder finalQuery, List<Object> queryParams) {
        String genre = request.getParameter("category");

        finalQuery.append(SQL_QUERY_PREFIX);
        finalQuery.append(SQL_QUERY_SUFFIX);

        if (genre != null && !genre.isEmpty()) {
            finalQuery.append(
                """
                 AND EXISTS (
                    SELECT 1
                    FROM genres_in_movies gm
                    LEFT JOIN genres g ON gm.genreId = g.id
                    WHERE gm.movieId = m.id AND g.name = ?
                )
                """
            );
            queryParams.add(genre);
        }

        addOrderByClauseToQuery(
                finalQuery,
                requestParams.firstSortKey,
                requestParams.firstSortOrder,
                requestParams.secondSortKey,
                requestParams.secondSortOrder
        );

        addLimitClauseToQuery(finalQuery, queryParams, requestParams.recordsPerPage);
        addOffsetClauseToQuery(finalQuery, queryParams, requestParams.currPage, requestParams.recordsPerPage);
    }

    private void buildInitialQuery(HttpServletRequest request, RequestParameters requestParams, StringBuilder finalQuery, List<Object> queryParams) {
        String initial = request.getParameter("category");

        finalQuery.append(SQL_QUERY_PREFIX);
        finalQuery.append(SQL_QUERY_SUFFIX);

        if (initial != null && !initial.isEmpty()) {
            if ("*".equals(initial)) {
                finalQuery.append(" AND m.title REGEXP '^[^a-zA-Z0-9]'");
            } else {
                finalQuery.append(" AND m.title LIKE ? ");
                queryParams.add(initial + "%");
            }
        }

        addOrderByClauseToQuery(
                finalQuery,
                requestParams.firstSortKey,
                requestParams.firstSortOrder,
                requestParams.secondSortKey,
                requestParams.secondSortOrder
        );

        addLimitClauseToQuery(finalQuery, queryParams, requestParams.recordsPerPage);
        addOffsetClauseToQuery(finalQuery, queryParams, requestParams.currPage, requestParams.recordsPerPage);
    }

    private void buildGenresQuery(StringBuilder finalQuery) {
        finalQuery.append(
            """
            SELECT DISTINCT g.name AS genres
            FROM genres g
            INNER JOIN genres_in_movies gm ON g.id = gm.genreId
            INNER JOIN movies ON gm.movieId = movies.id
            ORDER BY g.name ASC
            """
        );
    }

    private void addOrderByClauseToQuery(StringBuilder query, String firstSortKey, String firstOrder, String secondSortKey, String secondOrder) {
        String firstPrefixedKey = addTablePrefix(firstSortKey);
        String secondPrefixedKey = addTablePrefix(secondSortKey);

        String orderByClause;

        if ("m.title".equalsIgnoreCase(firstPrefixedKey)) {
            orderByClause = " ORDER BY LEFT(" + firstPrefixedKey + ", 1) " + firstOrder.toUpperCase()
                    + ", " + secondPrefixedKey + " " + secondOrder.toUpperCase()
                    + ", " + firstPrefixedKey + " " + (firstOrder.toUpperCase());

        } else if ("m.title".equalsIgnoreCase(secondPrefixedKey)) {
            orderByClause = " ORDER BY " + firstPrefixedKey + " " + firstOrder.toUpperCase()
                    + ", LEFT(" + secondPrefixedKey + ", 1) " + secondOrder.toUpperCase()
                    + ", " + secondPrefixedKey + " " + secondOrder.toUpperCase();

        } else {
            orderByClause = " ORDER BY "
                    + firstPrefixedKey
                    + " "
                    + (firstOrder != null ? firstOrder.toUpperCase() : "DESC")
                    + ", "
                    + secondPrefixedKey
                    + " "
                    + (secondOrder != null ? secondOrder.toUpperCase() : "ASC");
        }

        logger.info('\n' + orderByClause + '\n');
        query.append(orderByClause);
    }

    private String addTablePrefix(String key) {
        if ("rating".equals(key)) {
            return "subquery." + key;
        } else {
            return "m." + key;
        }
    }

    private void addLimitClauseToQuery(StringBuilder query, List<Object> queryParams, Integer recordsPerPage) {
        query.append(" LIMIT ? ");
        queryParams.add(recordsPerPage);
    }

    private void addOffsetClauseToQuery(StringBuilder query, List<Object> queryParams, Integer currPage, Integer recordsPerPage) {
        int offset = (currPage - 1) * recordsPerPage;
        query.append(" OFFSET ? ");
        queryParams.add(offset);
    }
}

