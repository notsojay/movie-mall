SELECT
    m.id AS movie_id,
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
    ) AS star_name_id_pairs,
    COUNT(*) OVER() AS total_records
FROM movies m
LEFT JOIN (
    SELECT movieId, rating
    FROM ratings
) AS subquery ON m.id = subquery.movieId
AND m.title LIKE 'A%'
ORDER BY subquery.rating DESC, m.title ASC;
