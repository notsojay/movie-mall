SELECT
    m.title,
    m.year,
    m.director,
    (
        SELECT GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC)
        FROM genres_in_movies gm
        LEFT JOIN genres g ON gm.genreId = g.id
        WHERE gm.movieId = m.id
    ) AS genres,
    (
        SELECT GROUP_CONCAT(DISTINCT CONCAT(s.name, '|', s.id) ORDER BY s.name ASC)
        FROM stars_in_movies sm
        LEFT JOIN stars s ON sm.starId = s.id
        WHERE sm.movieId = m.id
    ) AS star_name_id_pairs,
    (
        SELECT r.rating
        FROM ratings r
        WHERE r.movieId = m.id
    ) AS rating
FROM movies m
WHERE m.id = 'tt0094859';
