import React, {useState, useEffect} from 'react';
import {Link, useSearchParams} from 'react-router-dom';
import {renderStarAsHeader, renderMovieTitle, renderBasicProperty} from '../utils/renderUtils';

function StarDetail() {
    const [starDetail, setStarDetail] = useState({
        star_name: '',
        star_birth_year: '',
        movie_ids: [],
        movie_titles: [],
        movie_directors: [],
        movie_release_years: []
    });
    const [searchParams] = useSearchParams();
    const star_id = searchParams.get('query');

    useEffect(() => {
        if (!star_id) return;

        fetch(`/moviemall_server_war_exploded/StarDetailServlet?query=${star_id}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('ERROR: Network response was not ok');
                }
                return response.json();
            })
            .then(data => setStarDetail(data))
            .catch(error => console.error('ERROR: Fetching the star details:', error))
    }, [star_id]);

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
                                            <td>
                                                {renderMovieTitle(starDetail.movie_ids[index], title)}
                                            </td>
                                            <td>
                                                {renderBasicProperty(starDetail.movie_release_years[index])}
                                            </td>
                                            <td>
                                                {renderBasicProperty(starDetail.movie_directors[index])}
                                            </td>
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
