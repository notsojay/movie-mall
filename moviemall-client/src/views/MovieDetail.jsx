import React, {useState, useEffect} from 'react';
import {useSearchParams} from "react-router-dom";
import {renderMovieTitleAsHeader, renderMovieGenresAsLink, renderBasicProperty} from '../utils/movieRenderers';
import {renderStarsAsLink} from '../utils/starRenderers';
import {API_PATH} from "../config/servletPaths";
import {APP_ROUTES} from "../config/appRoutes";
import {addToCart, fetchData, postData} from "../utils/apiCaller";

import '../assets/styles/table.css';
import '../assets/styles/header.css';
import '../assets/styles/link.css';
import '../assets/styles/page.css';

function MovieDetail() {
    const [movieDetail, setMovieDetail] = useState({
        title: '',
        year: '',
        director: '',
        genres: [],
        star_names: [],
        star_ids: [],
        rating: '',
        price: ''
    });
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchParams] = useSearchParams();
    const movie_id = searchParams.get('query');

    useEffect(() => {
        fetchData(API_PATH.MOVIE_DETAIL, { query: movie_id}, false, "Error fetching star details")
            .then(data => setMovieDetail(data))
            .catch(err => setError(err.message))
            .finally(() => setIsLoading(false));
    }, [movie_id]);


    if (isLoading) {
        return null;
    }

    if (error) {
        return <p>Error: {error}</p>;
    }

    return (
        <div className="page">
            {movieDetail?.title ? (
                <React.Fragment>
                    <h1>
                        {renderMovieTitleAsHeader(movieDetail.title, movieDetail.year)}
                    </h1>
                    <table>
                        <thead>
                            <tr>
                                <th>Director</th>
                                <th>Genres</th>
                                <th>Stars</th>
                                <th>Rating</th>
                                <th>Cart</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr key={movie_id}>
                                <td>{renderBasicProperty(movieDetail.director)}</td>
                                <td>{renderMovieGenresAsLink(movieDetail.genres, APP_ROUTES.MOVIE_LIST, true, 3)}</td>
                                <td>{renderStarsAsLink(movieDetail.star_names, movieDetail.star_ids, APP_ROUTES.STAR_DETAIL)}</td>
                                <td>{renderBasicProperty(movieDetail.rating)}</td>
                                <td>
                                    <button className="cart-custom-button" onClick={() => addToCart(movie_id, movieDetail.title, movieDetail.price)}>
                                        Add to cart
                                    </button>
                                </td>
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
