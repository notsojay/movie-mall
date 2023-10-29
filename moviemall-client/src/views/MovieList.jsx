import React, {useEffect, useState} from 'react';
import {useSearchParams} from 'react-router-dom';
import {renderBasicProperty, renderH1, renderMovieGenresAsLink, renderMovieTitleAsLink} from '../utils/movieRenderers';
import {renderStarsAsLink} from '../utils/starRenderers';
import {fetchData} from "../utils/apiCaller";
import {API_PATH} from "../config/servletPaths";
import {APP_ROUTES} from "../config/appRoutes";
import {REQUEST_TYPE} from "../config/movieRequestTypes";
import {FilterOptions} from "../components/FilterOptions";
import {PaginationButtons} from "../components/PaginationButtons";

import '../assets/styles/table.css';
import '../assets/styles/header.css';
import '../assets/styles/link.css';

function MovieList() {
    const [movies, setMovies] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchParams] = useSearchParams();
    const requestType = searchParams.get('requestType');
    const genre = searchParams.get('genre');
    const initial = searchParams.get('initial');

    const [recordsPerPage, setRecordsPerPage] = useState(() => {
        const storedRecordsPerPage = localStorage.getItem('recordsPerPage');
        return storedRecordsPerPage ? JSON.parse(storedRecordsPerPage) : 25;
    });
    const [totalPages, setTotalPages] = useState(() => {
        const storedTotalPage = localStorage.getItem('totalPage');
        return storedTotalPage ? JSON.parse(storedTotalPage) : 1;
    });
    const [currentFirstSortKey, setCurrentFirstSortKey] = useState(() => {
        const storedCurrentFirstSortKey = localStorage.getItem('currentFirstSortKey');
        return storedCurrentFirstSortKey || 'rating';
    });
    const [currentFirstSortOrder, setCurrentFirstSortOrder] = useState(() => {
        const storedCurrentFirstSortOrder = localStorage.getItem('currentFirstSortOrder');
        return storedCurrentFirstSortOrder || 'desc';
    });
    const [currentSecondSortKey, setCurrentSecondSortKey] = useState(() => {
        const storedCurrentSecondSortKey = localStorage.getItem('currentSecondSortKey');
        return storedCurrentSecondSortKey || 'title';
    });
    const [currentSecondSortOrder, setCurrentSecondSortOrder] = useState(() => {
        const storedCurrentSecondSortOrder = localStorage.getItem('currentSecondSortOrder');
        return storedCurrentSecondSortOrder || 'asc';
    });

    const getCurrentUrlType = () => {
        const path = window.location.pathname;
        const routes = [
            APP_ROUTES.MOVIE_LIST_BY_GENRE,
            APP_ROUTES.MOVIE_LIST_BY_INITIAL,
        ];
        return routes.find(route => path.includes(route)) || null;
    };

    const currentUrlType = getCurrentUrlType();
    const storageKey = `${currentUrlType}_currPage`;
    const [currentPage, setCurrentPage] = useState(() => {
        const storedPage = localStorage.getItem(storageKey);
        return storedPage ? JSON.parse(storedPage) : 1;
    });

    const nextPage = () => {
        setCurrentPage(prev => prev + 1);
    };

    const prevPage = () => {
        if (currentPage > 1) setCurrentPage(prev => prev - 1);
    };

    const filterOptionsProps = {
        requestType,
        recordsPerPage,
        setRecordsPerPage,
        setCurrentFirstSortKey,
        setCurrentFirstSortOrder,
        setCurrentSecondSortKey,
        setCurrentSecondSortOrder
    };

    const paginationProps = {
        requestType,
        prevPage,
        nextPage,
        currentPage,
        totalPages
    };

    useEffect(() => {
        let params = {
            recordsPerPage: recordsPerPage,
            firstSortKey: currentFirstSortKey,
            firstSortOrder: currentFirstSortOrder,
            secondSortKey: currentSecondSortKey,
            secondSortOrder: currentSecondSortOrder
        };

        switch (requestType) {
            case REQUEST_TYPE.GET_TOP20_MOVIES:
                params.requestType = REQUEST_TYPE.GET_TOP20_MOVIES;
                break;
            case REQUEST_TYPE.BROWSE_MOVIES_BY_GENRE:
                params.requestType = REQUEST_TYPE.BROWSE_MOVIES_BY_GENRE;
                params.genre = genre;
                params.page = currentPage
                break;
            case REQUEST_TYPE.BROWSE_MOVIES_BY_INITIAL:
                params.requestType = REQUEST_TYPE.BROWSE_MOVIES_BY_INITIAL;
                params.initial = initial;
                params.page = currentPage
                break;
            default:
                console.error("Unknown requestType:", requestType);
                return;
        }

        fetchData(API_PATH.MOVIE_LIST, params, "Error fetching movies")
            .then(data => setMovies(data))
            .catch(err => setError(err.message))
            .finally(() => setIsLoading(false));

        setTotalPages(Math.ceil(movies?.[0]?.total_records / recordsPerPage));

    }, [currentFirstSortOrder, currentFirstSortKey, currentSecondSortOrder, currentSecondSortKey, genre, initial, currentPage, recordsPerPage, requestType, movies]);

    useEffect(() => {
        localStorage.setItem(storageKey, currentPage);
        localStorage.setItem('recordsPerPage', recordsPerPage);
        localStorage.setItem('totalPages', totalPages);
        localStorage.setItem('currentFirstSortKey', `${currentFirstSortKey}`);
        localStorage.setItem('currentFirstSortOrder', `${currentFirstSortOrder}`);
        localStorage.setItem('currentSecondSortKey', `${currentSecondSortKey}`);
        localStorage.setItem('currentSecondSortOrder', `${currentSecondSortOrder}`);
    }, [currentPage, recordsPerPage, currentFirstSortKey, currentFirstSortOrder, currentSecondSortKey, currentSecondSortOrder, totalPages, storageKey]);

    if (isLoading) {
        return null;
    }

    if (error) {
        return <p>Error: {error}</p>;
    }

    return (
        <div>
            <h1>{renderH1(requestType, genre, initial)}</h1>
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