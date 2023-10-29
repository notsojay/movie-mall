// Renders star names as links
import React from "react";
import {Link} from "react-router-dom";

import '../assets/styles/table.css';
import '../assets/styles/header.css';

export function renderStarsAsLink(starNames, starIds, baseLink, truncate = false, numOfRemain = 0) {
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
                <Link className="link"
                      to={`${baseLink}?query=${starId}`}>
                    {starName}
                </Link>
                {(displayedStars.length - 1 !== starIndex) && <span className="comma"> · </span>}
            </React.Fragment>
        );
    });

    if (truncate && starNames.length > numOfRemain) renderedStars.push('...');
    return renderedStars;
}

export function renderStarAsHeader(starName, starBirthYear) {
    if (!starName) return 'N/A';
    return (
        <span>
            {starName}
            <span className="year-class">(Date Of Birth: {starBirthYear ?? 'N/A'})</span>
        </span>
    )
}