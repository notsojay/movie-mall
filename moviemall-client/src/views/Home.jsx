import React, {useEffect, useState} from 'react';
import {renderMovieGenresAsLink, renderMovieInitialsAsLink} from "../utils/movieRenderers";
import {REQUEST_TYPE} from "../config/movieRequestTypes";
import {API_PATH} from "../config/servletPaths";

import '../assets/styles/link.css';
import '../assets/styles/header.css';
import {fetchData} from "../utils/apiCaller";

export default function Home() {
    const letters = [...'ABCDEFGHIJKLMNOPQRSTUVWXYZ', ...'0123456789', '*'];
    const [genres, setGenres] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        fetchData(API_PATH.MOVIE_LIST, {requestType: REQUEST_TYPE.GET_ALL_GENRES}, "Error fetching genres")
            .then(data => data.map(item => item.genres))
            .then(data => data.flat())
            .then(data => setGenres(data))
            .catch(err => setError(err.message))
            .finally(() => setIsLoading(false));
    }, []);

    return (
        <div className="browsing">
            <div className="genres">
                <h1>Browsing by movie genres</h1>
                {renderMovieGenresAsLink(genres, '/movie-list')}
            </div>

            <div className="initials">
                <h1>Browsing by movie initials</h1>
                {renderMovieInitialsAsLink(letters, '/movie-list')}
            </div>
        </div>
    );
}
