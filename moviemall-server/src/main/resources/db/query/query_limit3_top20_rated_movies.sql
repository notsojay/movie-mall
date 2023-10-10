SELECT
    movie_id,
    m.title,
    m.year,
    m.director,
    subquery.rating,
    (
        SELECT GROUP_CONCAT(DISTINCT limited_genres.name ORDER BY limited_genres.name ASC)
        FROM (
            SELECT g.name
            FROM genres_in_movies gm
            LEFT JOIN genres g ON gm.genreId = g.id
            WHERE gm.movieId = m.id
            LIMIT 3
        ) AS limited_genres
    ) AS genres,
    (
        SELECT GROUP_CONCAT(DISTINCT CONCAT(limited_stars.name, '|', limited_stars.id) ORDER BY limited_stars.name ASC)
        FROM (
            SELECT s.name, s.id
            FROM stars_in_movies sm
            LEFT JOIN stars s ON sm.starId = s.id
            WHERE sm.movieId = m.id
            LIMIT 3
        ) AS limited_stars
    ) AS star_name_id_pairs
FROM (
    SELECT
        m.id AS movie_id,
        r.rating
    FROM movies m
    LEFT JOIN ratings r ON m.id = r.movieId
    ORDER BY r.rating DESC
    LIMIT 20
) AS subquery
LEFT JOIN movies m ON m.id = subquery.movie_id
GROUP BY subquery.movie_id, subquery.rating
ORDER BY subquery.rating DESC;
