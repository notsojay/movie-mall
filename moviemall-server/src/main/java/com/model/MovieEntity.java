package com.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MovieEntity {

    @JsonProperty("movie_id")
    private String movieId = null;

    private String title = null;

    private Integer year = null;

    private String director = null;

    private List<String> genres = null;

    @JsonProperty("star_ids")
    private List<String> starIds = null;

    @JsonProperty("star_names")
    private List<String> starNames = null;

    private Float rating = null;

    public MovieEntity() {

    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getStarIds() {
        return starIds;
    }

    public void setStarIds(List<String> starIds) {
        this.starIds = starIds;
    }

    public List<String> getStarNames() {
        return starNames;
    }

    public void setStarNames(List<String> starNames) {
        this.starNames = starNames;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }
}
