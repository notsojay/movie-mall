import React, {useEffect, useState} from 'react';
import {useSearchParams} from 'react-router-dom';
import {renderBasicProperty, renderH1, renderMovieGenresAsLink, renderMovieTitleAsLink} from '../utils/movieRenderers';
import {renderStarsAsLink} from '../utils/starRenderers';
import {fetchData, addToCart} from "../utils/apiCaller";
import {SERVLET_ROUTE} from "../config/servletRoutes";
import {APP_ROUTES} from "../config/appRoutes";
import {REQUEST_TYPE} from "../config/movieRequestTypes";
import {FilterOptions} from "../components/FilterOptions";
import {PaginationButtons} from "../components/PaginationButtons";

import '../assets/styles/table.css';
import '../assets/styles/header.css';
import '../assets/styles/link.css';
import '../assets/styles/page.css';
import {getCookie, setCookie} from "../utils/cookie";
import SubscribeSection from "../components/SubscribeSection";

function MovieList() {
    const [movies, setMovies] = useState([]);
    const [totalPages, setTotalPages] = useState(null)
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchParams] = useSearchParams();
    const requestType = searchParams.get('requestType');
    let category = null;
    let searchQuery = null;

    switch (requestType) {
        case REQUEST_TYPE.BROWSE_MOVIES_BY_GENRE:
            category = searchParams.get('genre');
            break;
        case REQUEST_TYPE.BROWSE_MOVIES_BY_INITIAL:
            category = searchParams.get('initial');
            break;
        case REQUEST_TYPE.SEARCH_MOVIES:
            searchQuery = {
                title: searchParams.get('title') || '',
                year: searchParams.get('year') || '',
                director: searchParams.get('director') || '',
                starName: searchParams.get('starName') || '',
            };
            let allEmpty = true;
            for (let key in searchQuery) {
                if (searchQuery[key] !== '') {
                    allEmpty = false;
                    break;
                }
            }
            if (allEmpty) {
                searchQuery = null;
                break;
            }
            const queryStr = Object.values(searchQuery).filter(Boolean).join('_');
            category = queryStr ? `search_${queryStr}` : 'search';
            break;
        default:
            break;
    }

    const [settings, setSettings] = useState(() => {
        const key = `setting_${requestType}_${category}`;
        const storedSettings = getCookie(key);
        console.log("store = " + storedSettings)
        return storedSettings ? JSON.parse(storedSettings) : {
            recordsPerPage: 25,
            initialSortValue: 'rating-desc-title-asc',
            currentPage: 1
        };
    });

    const updateSetting = (newSetting) => {
        const key = `setting_${requestType}_${category}`;
        // console.log(key);
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
        const valueArray = settings?.initialSortValue?.split('-');

        let params = {
            requestType,
            category,
            ...searchQuery,
            ...settings,
            firstSortKey: valueArray?.[0] ?? "rating", // "title"
            firstSortOrder: valueArray?.[1] ?? "desc", // "asc"
            secondSortKey: valueArray?.[2] ?? "title", // "rating"
            secondSortOrder: valueArray?.[3] ?? "asc"// "asc"
        };

         fetchData(SERVLET_ROUTE.MOVIE_LIST, params, true, "Error fetching movie list")
            .then(response => {
                if (response.status === 200) {
                    setMovies(response.data);
                    const newTotalPages = Math.ceil(response.data[0]?.total_records / settings.recordsPerPage);
                    setTotalPages(newTotalPages);
                    console.log("total pages: " + newTotalPages);
                    if (settings?.currentPage > newTotalPages) {
                        updateSetting({currentPage: 1});
                    }
                } else {
                    throw new Error('Failed to fetch movie list: status code ' + response.status);
                }
            })
            .catch(err => setError(err.message))
            .finally(() => setIsLoading(false));

    }, [category, searchParams, settings.recordsPerPage, settings.initialSortValue, settings.currentPage]);


    useEffect(() => {
        console.log(settings.currentPage);
        window.scrollTo(0, 0);
    }, [settings.currentPage, searchParams]);

    if (isLoading) {
        return null;
    }

    if (error) {
        return <p>Error: {error}</p>;
    }

    return (
        <div>
            <div className="video-background">
                <video autoPlay loop muted>
                    <source src={process.env.PUBLIC_URL + '/videos/background2.mp4'} type="video/mp4" />
                </video>
            </div>
            <h1>{renderH1(requestType, category)}</h1>
            {movies?.length > 0 && <FilterOptions {...filterOptionsProps} />}
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
                            <th>Cart</th>
                        </tr>
                    </thead>
                    <tbody>
                    {Array.isArray(movies) && movies.map((movie) => (
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
                <p className="empty-movie">It seems there are no movies that match at this time :( </p>
            )}
            {Array.isArray(movies) && movies[0]?.total_records > settings.recordsPerPage && <PaginationButtons {...paginationProps} />}
        </div>
    );
}

export default MovieList;