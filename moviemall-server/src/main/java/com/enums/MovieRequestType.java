package com.enums;

public enum MovieRequestType {
    GET_TOP20_MOVIES,
    SEARCH_MOVIES,
    BROWSE_MOVIES_BY_GENRE,
    BROWSE_MOVIES_BY_INITIAL,
    GET_ALL_GENRES;

    public String toString() {
        return this.name().toLowerCase().replace("_", "-");
    }

    public static MovieRequestType fromString(String value) {
        String enumValue = value.toUpperCase().replace("-", "_");
        return MovieRequestType.valueOf(enumValue);
    }
}