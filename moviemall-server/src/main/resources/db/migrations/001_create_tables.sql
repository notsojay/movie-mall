# DROP DATABASE IF EXISTS moviedb;
CREATE DATABASE moviedb;
USE moviedb;

CREATE TABLE movies
(
    id       VARCHAR(10),
    title    VARCHAR(100) NOT NULL,
    year     INT          NOT NULL,
    director VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE stars
(
    id        VARCHAR(10),
    name      VARCHAR(100) NOT NULL,
    birthYear INT,
    PRIMARY KEY (id)
);

CREATE TABLE stars_in_movies
(
    starId  VARCHAR(10) NOT NULL,
    movieId VARCHAR(10) NOT NULL,
    FOREIGN KEY (starId) REFERENCES Stars (id),
    FOREIGN KEY (movieId) REFERENCES movies (id)
);

CREATE TABLE genres
(
    id   INT AUTO_INCREMENT,
    name VARCHAR(32) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE genres_in_movies
(
    genreId INT         NOT NULL,
    movieId VARCHAR(10) NOT NULL,
    FOREIGN KEY (genreId) REFERENCES genres (id),
    FOREIGN KEY (movieId) REFERENCES movies (id)
);

CREATE TABLE creditCards
(
    id         VARCHAR(20),
    firstName  VARCHAR(50) NOT NULL,
    lastName   VARCHAR(50) NOT NULL,
    expiration DATE        NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE customers
(
    id           INT AUTO_INCREMENT,
    firstName    VARCHAR(50)  NOT NULL,
    lastName     VARCHAR(50)  NOT NULL,
    creditCardId VARCHAR(20)  NOT NULL,
    address      VARCHAR(200) NOT NULL,
    email        VARCHAR(50)  NOT NULL,
    password     VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (creditCardId) REFERENCES creditCards (id)
);

CREATE TABLE sales
(
    id         INT AUTO_INCREMENT,
    customerId INT         NOT NULL,
    moviesId   VARCHAR(10) NOT NULL,
    saleDate   DATE        NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (customerId) REFERENCES customers (id),
    FOREIGN KEY (moviesId) REFERENCES movies (id)
);

CREATE TABLE ratings
(
    movieId  VARCHAR(10) NOT NULL,
    rating   FLOAT       NOT NULL,
    numVotes INT         NOT NULL,
    FOREIGN KEY (movieId) REFERENCES movies (id)
);

CREATE INDEX idx_ratings_rating ON ratings (rating) USING BTREE;

CREATE INDEX idx_gim_movieId ON genres_in_movies (movieId) USING BTREE;

CREATE INDEX idx_sim_movieId ON stars_in_movies (movieId) USING BTREE;

CREATE INDEX idx_sim_starId ON stars_in_movies (starId) USING HASH;

CREATE INDEX idx_ratings_movieId ON ratings (movieId) USING HASH;
