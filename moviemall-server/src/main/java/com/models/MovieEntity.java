package com.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MovieEntity {

    @JsonProperty("movie_id")
    private String movieId = null;

    private String title = null;

    private Integer year = null;

    private Float price = null;

    private String director = null;

    @JsonProperty("main_genre")
    private String mainGenre = null;

    private List<String> genres = null;

    @JsonProperty("lead_star_name")
    private String leadStarName = null;

    @JsonProperty("lead_star_birth_year")
    private Integer leadStarBirthYear = null;

    @JsonProperty("star_ids")
    private List<String> starIds = null;

    @JsonProperty("star_names")
    private List<String> starNames = null;

    private Float rating = null;

    @JsonProperty("total_records")
    private Integer totalRecords;

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

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getLeadStarName() {
        return leadStarName;
    }

    public void setLeadStarName(String leadStarName) {
        this.leadStarName = leadStarName;
    }

    public Integer getLeadStarBirthYear() {
        return leadStarBirthYear;
    }

    public void setLeadStarBirthYear(Integer leadStarBirthYear) {
        this.leadStarBirthYear = leadStarBirthYear;
    }

    public String getMainGenre() {
        return mainGenre;
    }

    public void setMainGenre(String mainGenre) {
        this.mainGenre = mainGenre;
    }
}
