import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { renderStarAsHeader, renderMovieTitle, renderBasicProperty } from '../utils/renderUtils';

import '../assets/styles/table.css';
import '../assets/styles/header.css';

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

    const fetchStarDetails = async () => {
        try {
            const response = await fetch(`/moviemall-server/StarDetailServlet?query=${star_id}`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            setStarDetail(data);
        } catch (err) {
            setError(err.message);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        (async () => {
            if (star_id) {
                await fetchStarDetails();
            } else {
                setIsLoading(false);
            }
        })();
    }, [star_id]);

    if (isLoading) {
        return null;
    }

    if (error) {
        return <p>Error: {error}</p>;
    }

    return (
        <div>
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
                                        <td>{renderMovieTitle(starDetail.movie_ids[index], title)}</td>
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
