package com.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.models.MovieEntity;

import org.json.JSONObject;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.utils.DatabaseManager.*;
import static com.utils.URLUtils.*;

public class MovieAdapter {

    private static final Map<String, BiConsumer<MovieEntity, String>> STRING_SETTERS = Map.of(

            "movie_id", (movie, value) -> movie.setMovieId(encodeToBase64(value)),

            "title", MovieEntity::setTitle,

            "director", MovieEntity::setDirector,

            "genres", (movie, value) -> movie.setGenres(Arrays.asList(value.split(","))),

            "total_records", (movie, value) -> movie.setTotalRecords(Integer.parseInt(value)),

            "star_name_id_pairs", (movie, value) -> {
                List<String> names = new ArrayList<>();
                List<String> ids = new ArrayList<>();

                for (String nameIdPair : value.split(",")) {
                    String[] parts = nameIdPair.split("\\|");
                    names.add(parts[0]);
                    ids.add(encodeToBase64(parts[1]));
                }
                movie.setStarNames(names);
                movie.setStarIds(ids);
            }
    );

    public static MovieEntity extractMovieFromDbResultSet(ResultSet rs) {
        MovieEntity movie = new MovieEntity();

        for (Map.Entry<String, BiConsumer<MovieEntity, String>> entry : STRING_SETTERS.entrySet()) {
            String value = getSafeColumnValue(rs, entry.getKey(), ResultSet::getString);
            if (value != null) {
                entry.getValue().accept(movie, value);
            }
        }

        Integer year = getSafeColumnValue(rs, "year", ResultSet::getInt);
        if (year != null) {
            movie.setYear(year);
        }

        Float rating = getSafeColumnValue(rs, "rating", ResultSet::getFloat);
        if (rating != null) {
            movie.setRating(rating);
        }

        Float price = getSafeColumnValue(rs, "price", ResultSet::getFloat);
        if (price != null) {
            movie.setPrice(price);
        }

        return movie;
    }

    public static JSONObject convertMovieDtoToJson(MovieEntity movie) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String jsonStr = mapper.writeValueAsString(movie);
        return new JSONObject(jsonStr);
    }
}