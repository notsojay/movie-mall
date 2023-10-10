import React, { useState, useEffect } from 'react';
import { useSearchParams, Link } from "react-router-dom";
import { renderMovieTitle, renderMovieGenres, renderStarsAsLink, renderBasicProperty } from '../utils/renderUtils';

function MovieDetail() {
    const [movieDetail, setMovieDetail] = useState({
        title: '',
        year: '',
        director: '',
        genres: [],
        star_names: [],
        star_ids: [],
        rating: ''
    });

    const [searchParams] = useSearchParams();
    const movie_id = searchParams.get('query');

    useEffect(() => {
        if (!movie_id) return;

        fetch(`/moviemall_server_war_exploded/MovieDetailServlet?query=${movie_id}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('ERROR: Network response was not ok');
                }
                return response.json();
            })
            .then(data => setMovieDetail(data))
            .catch(error => console.error('ERROR: Fetching the movie details:', error));
    }, [movie_id]);

    return (
        <div>
            {movieDetail?.title ? (
                <React.Fragment>
                    <h1>
                        {renderMovieTitle(movie_id, movieDetail.title, movieDetail.year, false)}
                    </h1>
                    <table>
                        <thead>
                            <tr>
                                <th>Director</th>
                                <th>Genres</th>
                                <th>Stars</th>
                                <th>Rating</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr key={movie_id}>
                                <td>{renderBasicProperty(movieDetail.director)}</td>
                                <td>{renderMovieGenres(movieDetail.genres, false)}</td>
                                <td>{renderStarsAsLink(movieDetail.star_names, movieDetail.star_ids, '/star-detail', true, 3)}</td>
                                <td>{renderBasicProperty(movieDetail.rating)}</td>
                            </tr>
                        </tbody>
                    </table>
                </React.Fragment>
            ) : (
                <p>404: Movie not found</p>
            )}
        </div>
    );
}

export default MovieDetail;
