import {useEffect, useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import Autosuggest from 'react-autosuggest';
import useLogout from '../hooks/useLogout';
import {useAuth} from "../hooks/useAuth";

import '../assets/styles/customer-navbar.css';
import {REQUEST_TYPE} from "../config/movieRequestTypes";
import {APP_ROUTES} from "../config/appRoutes";
import {fetchData} from "../utils/apiCaller";
import {SERVLET_ROUTE} from "../config/servletRoutes";

function CustomerNavbar() {
    const { isLoggedIn, isChecking, showLoginModal, setShowLoginModal } = useAuth();
    const handleLogOut = useLogout();
    const [suggestions, setSuggestions] = useState([]);
    const [selectedSuggestion, setSelectedSuggestion] = useState(null);
    const [title, setTitle] = useState('');
    const [searchParams, setSearchParams] = useState({
        year: '',
        director: '',
        starName: '',
    });
    const navigate = useNavigate();
    let timeoutId = null;

    const fetchAutocompleteSuggestions = async (inputValue) => {
        if (!isLoggedIn) return;
        clearTimeout(timeoutId);
        if (inputValue.length >= 3) {
            timeoutId = setTimeout(async () => {
                const cachedSuggestions = sessionStorage.getItem(inputValue);
                if (cachedSuggestions) {
                    const parsedSuggestions = JSON.parse(cachedSuggestions);
                    if (Array.isArray(parsedSuggestions)) {
                        setSuggestions(parsedSuggestions);
                    } else {
                        console.error('Cached suggestions is not an array');
                        setSuggestions([]);
                    }
                } else {
                    try {
                        const response = await fetchData(SERVLET_ROUTE.AUTOCOMPLETE, {query: inputValue}, false, 'Error fetching suggestions');

                        if (response.status === 200) {
                            let suggestions = await response.data;
                            setSuggestions(suggestions);
                            sessionStorage.setItem(inputValue, JSON.stringify(suggestions));
                            console.log(suggestions)
                        } else {
                            throw new Error('Network response was not ok');
                        }
                    } catch (error) {
                        console.error('Error fetching suggestions:', error);
                        setSuggestions([]);
                    }
                }
            }, 300);
        } else {
            setSuggestions([]);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setSearchParams({ ...searchParams, [name]: value });
    };

    const renderSuggestion = (suggestion, { isHighlighted }) => (
        <div
            key={suggestion.id}
            onClick={() => handleSuggestionClick(suggestion)}
            className={`suggestion-item ${isHighlighted ? 'react-autosuggest__suggestion--highlighted' : ''}`}
        >
            {suggestion.title}
        </div>
    );

    const onSuggestionSelected = (event, { suggestion, method }) => {
        if (method === 'enter') {
            event.preventDefault();
            setSelectedSuggestion(suggestion);
            navigateToMoviePage(suggestion.id);
        }
    };

    const handleSuggestionClick = (suggestion) => {
        setTitle(suggestion.title);
        setSelectedSuggestion(suggestion);
        navigateToMoviePage(suggestion.id);
    };

    const handleSearch = () => {
        console.log(selectedSuggestion);
        console.log(title);
        if (selectedSuggestion) {
            navigateToMoviePage(selectedSuggestion.id);
            return;
        }
        const query = new URLSearchParams();
        if (title) query.append('title', title);
        Object.entries(searchParams).forEach(([key, value]) => {
            if (value) query.append(key, value);
        });
        navigate(`${APP_ROUTES.MOVIE_LIST}?requestType=${REQUEST_TYPE.SEARCH_MOVIES}&${query.toString()}`);
        setSearchParams({
            year: '',
            director: '',
            starName: ''
        });
        setTitle('');
        setSelectedSuggestion(null);
    };

    const navigateToMoviePage = (movieId) => {
        setTitle('');
        setSelectedSuggestion(null);
        setTimeout(() => {
            navigate(`${APP_ROUTES.MOVIE_DETAIL}?query=${movieId}`);
        }, 0);
    };

    useEffect(() => {
        if (title === '') {
            setTitle('');
            setSelectedSuggestion(null);
        }
    }, [title]);

    if (isChecking || showLoginModal) return null;

    return (
        <div className="navbar">
            <div className="navbar-section navbar-left">
                <Link className="transparent-custom-button" to={APP_ROUTES.HOME}>Home</Link>
                <Link className="transparent-custom-button" to={`${APP_ROUTES.MOVIE_LIST}?requestType=${REQUEST_TYPE.GET_TOP20_MOVIES}`}>Top 20 Movies</Link>
            </div>
            <div className="navbar-section navbar-center">
                <div className="search-bar">
                    <Autosuggest
                        suggestions={suggestions}
                        onSuggestionsFetchRequested={({ value }) => fetchAutocompleteSuggestions(value)}
                        onSuggestionsClearRequested={() => setSuggestions([])}
                        onSuggestionSelected={onSuggestionSelected}
                        getSuggestionValue={suggestion => suggestion.title}
                        renderSuggestion={renderSuggestion}
                        inputProps={{
                            placeholder: 'Title',
                            value: title,
                            onChange: (event, { newValue }) => {
                                setTitle(newValue);
                                setSearchParams({ ...searchParams, title: newValue });
                            },
                            className: 'react-autosuggest__input',
                            disabled: !isLoggedIn
                        }}
                        theme={{
                            suggestionsContainer: "suggestions-container",
                            suggestion: "suggestion-item"
                        }}
                    />
                    <input
                        className="year"
                        type="number"
                        placeholder="Year"
                        name="year"
                        value={searchParams.year}
                        onChange={handleInputChange}
                    />
                    <input
                        type="text"
                        placeholder="Director"
                        name="director"
                        value={searchParams.director}
                        onChange={handleInputChange}
                    />
                    <input
                        type="text"
                        placeholder="Star's Name"
                        name="starName"
                        value={searchParams.starName}
                        onChange={handleInputChange}
                    />
                    <Link className="transparent-custom-button" onClick={(e) => { e.preventDefault(); handleSearch(); }} to="#">
                        Search
                    </Link>
                </div>
            </div>
            <div className="navbar-section navbar-right">
                <Link className="transparent-custom-button" to={APP_ROUTES.SHOPPING_CART}>Checkout</Link>
                {isLoggedIn ? (
                    <Link className="transparent-custom-button" onClick={(e) => { e.preventDefault(); handleLogOut(); }} to="#">
                        Log out
                    </Link>
                ) : (
                    <>
                        <Link className="transparent-custom-button" onClick={(e) => { e.preventDefault(); setShowLoginModal(true); }} to="#">
                            Log in
                        </Link>
                    </>
                )}
            </div>
        </div>
    );
}

export default CustomerNavbar;