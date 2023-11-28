package edu.uci.ics.fabflixmobile.data.model;

import java.util.List;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    private final String title;
    private final String movieID;
    private Short year = null;
    private final String director;
    private final List<String> genres;
    private final List<String> stars;

    public Movie(String title, String movieID, Short year, String director, List<String> genres, List<String> stars) {
        this.title = title;
        this.movieID = movieID;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }

    public String getTitle() {
        return title;
    }

    public Short getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public List<String> getGenres() {
        return genres;
    }

    public List<String> getStars() {
        return stars;
    }

    public String getMovieID() {
        return movieID;
    }
}