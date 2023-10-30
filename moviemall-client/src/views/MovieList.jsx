import React, {useEffect, useRef, useState} from 'react';
import {useSearchParams} from 'react-router-dom';
import {renderBasicProperty, renderH1, renderMovieGenresAsLink, renderMovieTitleAsLink} from '../utils/movieRenderers';
import {renderStarsAsLink} from '../utils/starRenderers';
import {fetchData, addToCart} from "../utils/apiCaller";
import {API_PATH} from "../config/servletPaths";
import {APP_ROUTES} from "../config/appRoutes";
import {REQUEST_TYPE} from "../config/movieRequestTypes";
import {FilterOptions} from "../components/FilterOptions";
import {PaginationButtons} from "../components/PaginationButtons";

import '../assets/styles/table.css';
import '../assets/styles/header.css';
import '../assets/styles/link.css';
import '../assets/styles/page.css';
import {getCookie, setCookie} from "../utils/cookie";

function MovieList() {
    const [movies, setMovies] = useState([]);
    const [totalPages, setTotalPages] = useState(1)
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchParams] = useSearchParams();
    const requestType = searchParams.get('requestType');
    let category = null;

    switch (requestType) {
        case REQUEST_TYPE.BROWSE_MOVIES_BY_GENRE:
            category = searchParams.get('genre')
            break;
        case REQUEST_TYPE.BROWSE_MOVIES_BY_INITIAL:
            category = searchParams.get('initial')
            break;
        default:
            break;
    }

    const [settings, setSettings] = useState(() => {
        const key = `setting_${requestType}_${category}`;
        const storedSettings = getCookie(key);
        console.log(storedSettings)
        return storedSettings ? JSON.parse(storedSettings) : {
            recordsPerPage: 25,
            firstSortKey: 'rating',
            firstSortOrder: 'desc',
            secondSortKey: 'title',
            secondSortOrder: 'asc',
            initialSortValue: 'rating-desc-title-asc',
            currentPage: 1
        };
    });

    const updateSetting = (newSetting) => {
        const key = `setting_${requestType}_${category}`;
        setSettings(prevSettings => {
            const newSettings = { ...prevSettings, ...newSetting };
            setCookie(key, JSON.stringify(newSettings), 3);
            return newSettings;
        });
    }

    const filterOptionsProps = {
        requestType,
        recordsPerPage: settings?.recordsPerPage ?? 25,
        initialSortValue: settings?.initialSortValue ?? 'rating-desc-title-asc',
        updateSetting
    };

    const paginationProps = {
        requestType,
        currentPage: settings?.currentPage ?? 1,
        totalPages,
        updateSetting
    };

    useEffect(() => {
        const searchQuery = {
            title: searchParams.get('title') || '',
            year: searchParams.get('year') || '',
            director: searchParams.get('director') || '',
            starName: searchParams.get('starName') || '',
        };

        let params = {
            requestType,
            category,
            ...searchQuery,
            ...settings,
        };

        fetchData(API_PATH.MOVIE_LIST, params, true, "Error fetching movies")
            .then(data => {
                setMovies(data)
                const newTotalPages = Math.ceil(data[0]?.total_records / settings.recordsPerPage);
                setTotalPages(newTotalPages);
            })
            .catch(err => setError(err.message))
            .finally(() => setIsLoading(false));

    }, [category, searchParams, settings.recordsPerPage, settings.firstSortKey, settings.firstSortOrder, settings.secondSortKey, settings.secondSortOrder, settings.currentPage]);

    useEffect(() => {
        window.scrollTo(0, 0);
    }, [settings.currentPage]);

    if (isLoading) {
        return null;
    }

    if (error) {
        return <p>Error: {error}</p>;
    }

    return (
        <div>
            <h1>{renderH1(requestType, category)}</h1>
            <FilterOptions {...filterOptionsProps} />
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
                            <th>Cart</th>
                        </tr>
                    </thead>
                    <tbody>
                        {movies.map((movie) => (
                            <tr key={movie.movie_id}>
                                <td>{renderMovieTitleAsLink(movie.movie_id, movie.title, APP_ROUTES.MOVIE_DETAIL)}</td>
                                <td>{renderBasicProperty(movie.year)}</td>
                                <td>{renderBasicProperty(movie.director)}</td>
                                <td>{renderMovieGenresAsLink(movie.genres, APP_ROUTES.MOVIE_LIST, true, 3)}</td>
                                <td>{renderStarsAsLink(movie.star_names, movie.star_ids, APP_ROUTES.STAR_DETAIL, true, 3)}</td>
                                <td>{renderBasicProperty(movie.rating)}</td>
                                <td>
                                    <button className="cart-custom-button" onClick={() => addToCart(movie.movie_id, movie.title, movie.price)}>
                                        Add to cart
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            ) : (
                <p>No movies available at the moment.</p>
            )}
            <PaginationButtons {...paginationProps} />
        </div>
    );
}

export default MovieList;