# SELECT
#     m.title,
#     m.year,
#     m.director,
#     (
#         SELECT GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC)
#         FROM genres_in_movies gm
#         LEFT JOIN genres g ON gm.genreId = g.id
#         WHERE gm.movieId = m.id
#     ) AS genres,
#     (
#         SELECT GROUP_CONCAT(DISTINCT CONCAT(s.name, '|', s.id) ORDER BY s.name ASC)
#         FROM stars_in_movies sm
#         LEFT JOIN stars s ON sm.starId = s.id
#         WHERE sm.movieId = m.id
#     ) AS star_name_id_pairs,
#     (
#         SELECT r.rating
#         FROM ratings r
#         WHERE r.movieId = m.id
#     ) AS rating
# FROM movies m
# WHERE m.id = 'tt0094859';

SELECT
    m.title,
    m.year,
    m.director,
    m.price,
    (
        SELECT GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC)
        FROM genres_in_movies gm
        LEFT JOIN genres g ON gm.genreId = g.id
        WHERE gm.movieId = m.id
    ) AS genres,
    (
        SELECT GROUP_CONCAT(DISTINCT CONCAT(s.name, '|', s.id) ORDER BY star_movie_count DESC, s.name ASC)
        FROM stars_in_movies sim
        LEFT JOIN stars s ON sim.starId = s.id
        LEFT JOIN (
            SELECT smc.starId, COUNT(smc.movieId) AS star_movie_count
            FROM stars_in_movies smc
            GROUP BY smc.starId
        ) AS star_counts ON s.id = star_counts.starId
        WHERE sim.movieId = m.id
    ) AS star_name_id_pairs,
    (
        SELECT r.rating
        FROM ratings r
        WHERE r.movieId = m.id
    ) AS rating
FROM movies m
WHERE m.id = 'tt0094859';
