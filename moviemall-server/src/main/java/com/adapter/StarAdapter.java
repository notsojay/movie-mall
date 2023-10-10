package com.adapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.model.StarEntity;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.utils.DatabaseManager.getSafeColumnValue;
import static com.utils.URLUtils.encodeToBase64;

public class StarAdapter {

    private static final Map<String, BiConsumer<StarEntity, String>> STRING_SETTERS = Map.of(

            "star_name", StarEntity::setStarName,

            "star_birth_year", StarEntity::setStarBirthYear,

            "movie_infos", (star, value) -> {
                List<String> titles = new ArrayList<>();
                List<String> ids = new ArrayList<>();
                List<String> directors = new ArrayList<>();
                List<Integer> years = new ArrayList<>();

                for (String detail : value.split(",")) {
                    String[] parts = detail.split("\\|");
                    if (parts.length != 4) continue; // Check to ensure we have all the expected data points

                    titles.add(parts[0]);
                    ids.add(encodeToBase64(parts[1]));
                    directors.add(parts[2]);
                    try {
                        years.add(Integer.parseInt(parts[3]));
                    } catch (NumberFormatException e) {
                        // Handle the exception, for example, by setting a default value or logging an error.
                        years.add(null); // or 0, depending on how want to handle it.
                    }
                }
                star.setMovieTitles(titles);
                star.setMovieIds(ids);
                star.setMovieDirectors(directors);
                star.setMovieReleaseYears(years);
            }
    );

    public static StarEntity extractStarFromDbResultSet(ResultSet rs) {
        StarEntity star = new StarEntity();

        for (Map.Entry<String, BiConsumer<StarEntity, String>> entry : STRING_SETTERS.entrySet()) {
            String value = getSafeColumnValue(rs, entry.getKey(), ResultSet::getString);
            if (value != null) {
                entry.getValue().accept(star, value);
            }
        }

        return star;
    }

    public static JSONObject convertStarEntityToJson(StarEntity star) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        String jsonStr = mapper.writeValueAsString(star);
        return new JSONObject(jsonStr);
    }
}


//            "movie_title_id_pairs", (star, value) -> {
//                List<String> titles = new ArrayList<>();
//                List<String> ids = new ArrayList<>();
//
//                for (String titleIdPair : value.split(",")) {
//                    String[] parts = titleIdPair.split("\\|");
//                    titles.add(parts[0]);
//                    ids.add(encodeToBase64(parts[1]));
//                }
//                star.setMovieTitles(titles);
//                star.setMovieIds(ids);
//            },