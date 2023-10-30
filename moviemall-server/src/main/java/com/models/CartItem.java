package com.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CartItem {
    private String movieId = null;
    private String movieTitle = null;
    private Double moviePrice = null;
    private Integer quantity = null;

    public CartItem() {

    }

    public CartItem(String movieId, String movieTitle, Double moviePrice, Integer quantity) {
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.moviePrice = moviePrice;
        this.quantity = quantity;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public Double getMoviePrice() {
        return moviePrice;
    }

    public void setMoviePrice(Double moviePrice) {
        this.moviePrice = moviePrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void increaseQuantity(Integer quantity) {
        this.quantity += quantity;
    }
}
