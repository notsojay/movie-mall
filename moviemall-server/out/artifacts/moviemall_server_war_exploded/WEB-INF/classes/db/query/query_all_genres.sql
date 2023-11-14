SELECT DISTINCT g.name AS genres
FROM genres g
INNER JOIN genres_in_movies gm ON g.id = gm.genreId
INNER JOIN movies ON gm.movieId = movies.id
ORDER BY g.name ASC