package com.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StarEntity {

    @JsonProperty("star_name")
    private String starName = null;

    @JsonProperty("star_birth_year")
    private String starBirthYear = null;

    @JsonProperty("movie_directors")
    private List<String> movieDirectors = null;

    @JsonProperty("movie_release_years")
    private List<Integer> movieReleaseYears = null;

    @JsonProperty("movie_ids")
    private List<String> movieIds = null;

    @JsonProperty("movie_titles")
    private List<String> movieTitles = null;

    public StarEntity() {

    }

    public String getStarName() {
        return starName;
    }

    public void setStarName(String starName) {
        this.starName = starName;
    }

    public String getStarBirthYear() {
        return starBirthYear;
    }

    public void setStarBirthYear(String starBirthYear) {
        this.starBirthYear = starBirthYear;
    }

    public List<String> getMovieIds() {
        return movieIds;
    }

    public void setMovieIds(List<String> movieIds) {
        this.movieIds = movieIds;
    }

    public List<String> getMovieTitles() {
        return movieTitles;
    }

    public void setMovieTitles(List<String> movieTitles) {
        this.movieTitles = movieTitles;
    }

    public List<String> getMovieDirectors() {
        return movieDirectors;
    }

    public void setMovieDirectors(List<String> movieDirectors) {
        this.movieDirectors = movieDirectors;
    }

    public List<Integer> getMovieReleaseYears() {
        return movieReleaseYears;
    }

    public void setMovieReleaseYears(List<Integer> movieReleaseYears) {
        this.movieReleaseYears = movieReleaseYears;
    }
}
