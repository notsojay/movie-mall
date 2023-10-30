package com.utils;

import com.enums.MovieRequestType;

import java.util.ArrayList;
import java.util.List;

public class OtherUtils {

    public static <T> List<T> castObjToList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object item : (List<?>) obj) {
                if (clazz.isInstance(item)) {
                    result.add(clazz.cast(item));
                } else {
                    throw new IllegalArgumentException("ERROR: List contains items that are not of type " + clazz.getName());
                }
            }
        }
        return result;
    }

    public static <T> T getOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    public static String getOrDefault(String value, String defaultValue) {
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }

    public static String getStringFromRequestOrSessionOrDefault(String requestValue, String sessionValue, String defaultValue) {
        if (requestValue != null && !requestValue.isEmpty() && !requestValue.equals(sessionValue)) {
            return requestValue;
        }
        return sessionValue != null ? sessionValue : defaultValue;
    }

    public static MovieRequestType getMovieRequestTypeFromRequestOrSessionOrDefault(String requestValue, MovieRequestType sessionValue, MovieRequestType defaultValue) {
        if (requestValue != null && !requestValue.isEmpty()) {
            try {
                MovieRequestType requestType = MovieRequestType.fromString(requestValue);
                if (sessionValue == null || requestType != sessionValue) {
                    return requestType;
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("ERROR: Invalid request value: " + requestValue);
            }
        }
        return sessionValue != null ? sessionValue : defaultValue;
    }

    public static Integer getIntFromRequestOrSessionOrDefault(String requestValue, Integer sessionValue, int defaultValue) {
        if (requestValue != null && !requestValue.isEmpty()) {
            try {
                int intValue = Integer.parseInt(requestValue);
                if (sessionValue == null || intValue != sessionValue) {
                    return intValue;
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("ERROR: Invalid integer value: " + requestValue);
            }
        }
        return sessionValue != null ? sessionValue : defaultValue;
    }
}
