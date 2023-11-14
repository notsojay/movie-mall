import React, {useEffect, useState} from 'react';
import {renderMovieGenresAsLink, renderMovieInitialsAsLink} from "../utils/movieRenderers";
import {REQUEST_TYPE} from "../config/movieRequestTypes";
import {SERVLET_ROUTE} from "../config/servletRoutes";

import '../assets/styles/link.css';
import '../assets/styles/header.css';
import '../assets/styles/page.css';
import {fetchData} from "../utils/apiCaller";
import {useAuth} from "../hooks/useAuth";

function Home() {
    const letters = [...'ABCDEFGHIJKLMNOPQRSTUVWXYZ', ...'0123456789', '*'];
    const [genres, setGenres] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const { isLoggedIn, showLoginModal, setShowLoginModal, isChecking } = useAuth();
    const [text, setText] = useState("Movie-Mall");

    useEffect(() => {
        fetchData(SERVLET_ROUTE.MOVIE_LIST, {requestType: REQUEST_TYPE.GET_ALL_GENRES}, false, "Error fetching genres")
            .then(
                response => {
                    if (response.status === 200) {
                        return response.data.map(item => item.genres).flat();
                    } else {
                        throw new Error('Failed to fetch genres: status code ' + response.status);
                    }
                }
            )
            .then(data => data.flat())
            .then(data => setGenres(data))
            .catch(err => setError(err.message))
            .finally(() => setIsLoading(false));
    }, []);

    useEffect(() => {
        setText(showLoginModal === true ? null : "Movie-Mall");
    }, [showLoginModal]);

    return (
        <div>
            <div className="video-background">
                <video autoPlay loop muted>
                    <source src={process.env.PUBLIC_URL + '/videos/background1.mp4'} type="video/mp4" />
                </video>
            </div>
            {isLoggedIn ? (
                <div className="browsing">
                    <div className="genres">
                        <h2>Browsing By Movie Genres</h2>
                        {renderMovieGenresAsLink(genres, '/movie-list')}
                    </div>
                    <div className="initials">
                        <h2>Browsing By Movie Initials</h2>
                        {renderMovieInitialsAsLink(letters, '/movie-list')}
                    </div>
                </div>
            ) : <h1 className="home-h1">{text}</h1>}
        </div>
    );
}

export default Home;
