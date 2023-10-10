SELECT
    s.name AS star_name,
    s.birthYear AS star_birth_year,
    (
        SELECT GROUP_CONCAT(DISTINCT CONCAT(m.title, '|', m.id, '|', m.director, '|', m.year) ORDER BY m.title ASC)
        FROM stars_in_movies sm
        LEFT JOIN movies m ON sm.movieId = m.id
        WHERE sm.starId = s.id AND m.director IS NOT NULL AND m.year IS NOT NULL
    ) AS movie_infos
FROM stars s
WHERE s.id = 'nm0000179';