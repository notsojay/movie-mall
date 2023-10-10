import React, { useState, useEffect } from 'react';
import { renderMovieTitle, renderMovieGenres, renderStarsAsLink, renderBasicProperty } from '../utils/renderUtils';
import '../assets/styles/table.css';
import '../assets/styles/header.css';

function MovieList() {
    const [movies, setMovies] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    const fetchMovies = async () => {
        try {
            const response = await fetch('/moviemall_server_war_exploded/MovieListServlet');
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            setMovies(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        (async () => {
            await fetchMovies();
        })();
    }, []);

    if (isLoading) {
        return null;
    }

    if (error) {
        return <p>Error: {error}</p>;
    }

    return (
        <div>
            <h1>Top 20 rated movies</h1>
            {movies.length > 0 ? (
                <table>
                    <thead>
                        <tr>
                            <th>Title</th>
                            <th>Year</th>
                            <th>Director</th>
                            <th>Genres</th>
                            <th>Stars</th>
                            <th>Rating</th>
                        </tr>
                    </thead>
                    <tbody>
                        {movies.map((movie) => (
                            <tr key={movie.movie_id}>
                                <td>{renderMovieTitle(movie.movie_id, movie.title)}</td>
                                <td>{renderBasicProperty(movie.year)}</td>
                                <td>{renderBasicProperty(movie.director)}</td>
                                <td>{renderMovieGenres(movie.genres, true)}</td>
                                <td>{renderStarsAsLink(movie.star_names, movie.star_ids, '/star-detail', true, 3)}</td>
                                <td>{renderBasicProperty(movie.rating)}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            ) : (
                <p>No movies available at the moment.</p>
            )}
        </div>
    );
}

export default MovieList;