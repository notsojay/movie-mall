import React, {useState} from "react";
import {postData} from "../utils/apiCaller";
import {SERVLET_ROUTE} from "../config/servletRoutes";
import {useAuth} from "../hooks/useAuth";


function MovieAdder() {
    const [movieTitle, setMovieTitle] = useState('');
    const [releaseYear, setReleaseYear] = useState('');
    const [director, setDirector] = useState('');
    const [starName, setStarName] = useState('');
    const [birthYear, setBirthYear] = useState('');
    const [genre, setGenre] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [message, setMessage] = useState(null);
    const { isLoggedIn, showLoginModal, setShowLoginModal, isChecking } = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);

        try {
            const response = await postData(SERVLET_ROUTE.MOVIE_DETAIL, {
                title: movieTitle,
                year: releaseYear,
                director: director,
                lead_star_name: starName,
                lead_star_birth_year: birthYear || null,
                main_genre: genre
            }, false, "Error inserting movie.");

            if (response.status === 200) {
                setMessage({ type: 'success', text: `Success! movieID: ${response.data.movie_id}, starID: ${response.data.star_id}, genreID: ${response.data.genre_id}` });
            }
        } catch (error) {
            setMessage({ type: 'error', text: 'Error: Duplicated movie!' });
        } finally {
            setIsSubmitting(false);
        }
    };

    if (!isLoggedIn) {
        setShowLoginModal(true);
        return null;
    }

    return (
        <div className="adder-container">
            <h1>Add New Movie</h1>
            <form onSubmit={handleSubmit}>
                {message && (
                    <p className={`message ${message.type}`}>
                        {message.text}
                    </p>
                )}
                <div className="adder-input-group">
                    <input
                        type="text"
                        placeholder="Movie title"
                        value={movieTitle}
                        onChange={(e) => setMovieTitle(e.target.value)}
                        required
                    />
                    <input
                        type="number"
                        placeholder="Movie Release Year"
                        value={releaseYear}
                        onChange={(e) => setReleaseYear(e.target.value)}
                        required
                    />
                    <input
                        type="text"
                        placeholder="Director"
                        value={director}
                        onChange={(e) => setDirector(e.target.value)}
                        required
                    />
                    <input
                        type="text"
                        placeholder="Star Name"
                        value={starName}
                        onChange={(e) => setStarName(e.target.value)}
                        required
                    />
                    <input
                        type="number"
                        id="birthYear"
                        value={birthYear}
                        placeholder="Birth Year (optional)"
                        onChange={(e) => setBirthYear(e.target.value)}
                    />
                    <input
                        type="text"
                        placeholder="Genre"
                        value={genre}
                        onChange={(e) => setGenre(e.target.value)}
                        required
                    />
                </div>
                <button type="adder-submit" disabled={isSubmitting}>
                    Submit
                </button>
            </form>
        </div>
    );
}

export default MovieAdder;