import React, {useEffect, useState} from 'react';
import {useSearchParams} from 'react-router-dom';
import {SERVLET_ROUTE} from "../config/servletRoutes";
import axios from "axios";
import {renderStarAsHeader} from '../utils/starRenderers';
import {renderBasicProperty, renderMovieTitleAsLink} from '../utils/movieRenderers';
import '../assets/styles/table.css';
import '../assets/styles/header.css';
import '../assets/styles/link.css';
import '../assets/styles/page.css';
import {APP_ROUTES} from "../config/appRoutes";
import {fetchData} from "../utils/apiCaller";

function StarDetail() {
    const [starDetail, setStarDetail] = useState({
        star_name: '',
        star_birth_year: '',
        movie_ids: [],
        movie_titles: [],
        movie_directors: [],
        movie_release_years: []
    });
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchParams] = useSearchParams();
    const star_id = searchParams.get('query');

    useEffect(() => {
        fetchData(SERVLET_ROUTE.STAR_DETAIL, { query: star_id }, false, "Error fetching star details")
            .then(response => {
                if (response.status === 200) {
                    setStarDetail(response.data);
                } else {
                    throw new Error('Failed to fetch star detail: status code ' + response.status);
                }
            })
            .catch(err => setError(err.message))
            .finally(() => setIsLoading(false));
    }, [star_id]);

    if (isLoading) {
        return null;
    }

    if (error) {
        return <p>Error: {error}</p>;
    }

    return (
        <div className="page">
            {starDetail?.star_name ? (
                <React.Fragment>
                    <h1>
                        {renderStarAsHeader(starDetail.star_name, starDetail.star_birth_year)}
                    </h1>
                    {starDetail?.movie_titles?.length > 0 ? (
                        <table>
                            <thead>
                                <tr>
                                    <th>Movie Title</th>
                                    <th>Release year</th>
                                    <th>Director</th>
                                </tr>
                            </thead>
                            <tbody>
                                {starDetail.movie_titles.map((title, index) => (
                                    <tr key={index}>
                                        <td>{renderMovieTitleAsLink(starDetail.movie_ids[index], title, APP_ROUTES.MOVIE_DETAIL)}</td>
                                        <td>{renderBasicProperty(starDetail.movie_release_years[index])}</td>
                                        <td>{renderBasicProperty(starDetail.movie_directors[index])}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    ) : null}
                </React.Fragment>
            ) : (
                <p>404: Star not found</p>
            )}
        </div>
    );
}

export default StarDetail;
