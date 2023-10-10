import React from 'react';
import { Link } from 'react-router-dom';

// Renders a movie title as a link/text
function renderMovieTitle(movieId, movieTitle, year = null, shouldLink = true) {
    if (!movieId || !movieTitle) return 'N/A';

    if (shouldLink) {
        return (
            <Link className="movie-title-link"
                  to={`/movie-detail?query=${movieId}`}>
                {movieTitle}
            </Link>
        );
    }

    return year ? `${movieTitle} (${year})` : movieTitle;
}

// Renders genres, with optional truncation
function renderMovieGenres(genres, truncate = false) {
    if (!genres || !genres.length) return 'N/A';

    const displayedGenres = truncate ? genres.slice(0, 3) : genres;
    return displayedGenres.join(', ') + (truncate && genres.length > 3 ? '...' : '');
}

// Renders star names as links
function renderStarsAsLink(starNames, starIds, baseStarLink, truncate = false, numOfRemain = 0) {
    if (typeof starNames === 'string') {
        starNames = [starNames];
        starIds = [starIds];
    }

    if (!starNames || !starIds || starNames.length !== starIds.length) return 'N/A';

    const displayedStars = truncate ? starNames.slice(0, numOfRemain) : starNames;
    const displayedStarIds = truncate ? starIds.slice(0, numOfRemain) : starIds;

    const renderedStars = displayedStars.map((starName, starIndex) => {
        const starId = displayedStarIds[starIndex];
        return (
            <React.Fragment key={starIndex}>
                <Link className="star-name-link"
                      to={`${baseStarLink}?query=${starId}`}>
                    {starName}
                </Link>
                {(displayedStars.length - 1 !== starIndex) && <span className="comma"> · </span>}
            </React.Fragment>
        );
    });

    if (truncate && starNames.length > numOfRemain) renderedStars.push('...');
    return renderedStars;
}

function renderStarAsHeader(starName, starBirthYear) {
    if (!starName) return 'N/A';
    return `${starName} (${starBirthYear || 'N/A'})`;
}

// Renders a basic property
function renderBasicProperty(propertyValue) {
    return propertyValue || 'N/A';
}

export { renderStarAsHeader, renderMovieTitle, renderMovieGenres, renderStarsAsLink, renderBasicProperty };
