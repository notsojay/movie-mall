SELECT
    m.title, m.year, m.director, subquery.rating,
    (
        SELECT GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC)
        FROM genres_in_movies gm
        JOIN genres g ON gm.genreId = g.id
        WHERE m.id = gm.movieId
    ) AS genres,
    (
        SELECT GROUP_CONCAT(DISTINCT s.name ORDER BY s.name ASC)
        FROM stars_in_movies sm
        JOIN stars s ON sm.starId = s.id
        WHERE m.id = sm.movieId
    ) AS stars
FROM (
    SELECT m.id AS movieId, r.rating
    FROM movies m
    INNER JOIN ratings r ON m.id = r.movieId
    ORDER BY r.rating DESC
    LIMIT 20
) AS subquery
INNER JOIN movies m ON m.id = subquery.movieId
GROUP BY subquery.movieId, subquery.rating
ORDER BY subquery.rating DESC;
