import React, { useState, useEffect } from 'react';
import '../assets/styles/MovieList.css';
import { renderMovieTitle, renderMovieGenres, renderStarsAsLink, renderBasicProperty } from '../utils/renderUtils';

function MovieList() {
    const [movies, setMovies] = useState([]);

    useEffect(() => {
        fetch('/moviemall_server_war_exploded/MovieListServlet')
            .then(response => {
                if (!response.ok) {
                    throw new Error('ERROR: Network response was not ok');
                }
                return response.json();
            })
            .then(data => setMovies(data))
            .catch(error => console.error('ERROR: Fetching the movie data:', error));
    }, []);

    return (
        <div>
            <h1>
                Top 20 rated movies
            </h1>
            {movies?.length > 0 ? (
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
