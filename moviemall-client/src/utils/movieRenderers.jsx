import React from "react";
import {Link} from "react-router-dom";
import {REQUEST_TYPE} from "../config/movieRequestTypes";

import '../assets/styles/table.css';
import '../assets/styles/header.css';

export function renderMovieTitleAsLink(movieId, movieTitle, baseLink) {
    if (!movieId || !movieTitle) return 'N/A';

    return (
        <Link className="link" to={`${baseLink}?query=${movieId}`}>
            {movieTitle}
        </Link>
    );

}

export function renderMovieTitleAsHeader(movieTitle, year = null) {
    if (!movieTitle) return 'N/A';

    return (
        <span>
            {movieTitle}
            <span className="year-class">(Release Year: {year ?? 'N/A'})</span>
        </span>
    );
}

// Renders genres, with optional truncation
export function renderMovieGenresAsText(genres, truncate = false) {
    if (!genres || !genres.length) return 'N/A';

    const displayedGenres = truncate ? genres.slice(0, 3) : genres;
    return displayedGenres.join(', ') + (truncate && genres.length > 3 ? '...' : '');
}

export function renderMovieGenresAsLink(genres, baseLink, truncate = false, numOfRemain = 0) {
    if (typeof genres === 'string') {
        genres = [genres];
    }

    if (!genres || !genres.length) return null;

    const displayedGenres = truncate ? genres.slice(0, numOfRemain) : genres;

    return displayedGenres.map((genre, genreIndex) => {
        return (
            <React.Fragment key={genreIndex}>
                <Link className="link"
                      to={`${baseLink}?requestType=${REQUEST_TYPE.BROWSE_MOVIES_BY_GENRE}&genre=${genre}`}>
                    {genre}
                </Link>
                {(displayedGenres.length - 1 !== genreIndex) &&
                truncate ? <span className="comma"> · </span> :
                    <span className="genre-space">  </span>}
            </React.Fragment>
        );
    });
}

export function renderMovieInitialsAsLink(initials, baseLink) {
    if (!initials || !initials.length) return null;

    return initials.map((initial, initialIndex) => {
        return (
            <React.Fragment key={initialIndex}>
                <Link className="link"
                      to={`${baseLink}?requestType=${REQUEST_TYPE.BROWSE_MOVIES_BY_INITIAL}&initial=${initial}`}>
                    {initial}
                </Link>
                {(initials.length - 1 !== initialIndex) && <span className="initial-space">  </span>}
            </React.Fragment>
        );
    });
}

export function renderH1(requestType, category) {
    switch (requestType) {
        case REQUEST_TYPE.GET_TOP20_MOVIES:
            return "Top 20 Rated Movies";
        case REQUEST_TYPE.SEARCH_MOVIES:
            return "Search Result"
        case REQUEST_TYPE.BROWSE_MOVIES_BY_GENRE:
            return `Movies In ${category} Genre`;
        case REQUEST_TYPE.BROWSE_MOVIES_BY_INITIAL:
            return `Movies Starting With Letter '${category}'`;
        default:
            return "Movies";
    }
} // Renders a basic property

export function renderBasicProperty(propertyValue) {
    return propertyValue || 'N/A';
}