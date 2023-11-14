import React, {useState} from "react";
import {postData} from "../utils/apiCaller";
import {SERVLET_ROUTE} from "../config/servletRoutes";
import "../assets/styles/adder-form.css"

function StarAdder() {
    const [starName, setStarName] = useState('');
    const [birthYear, setBirthYear] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [message, setMessage] = useState(null);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsSubmitting(true);

        try {
            const response = await postData(SERVLET_ROUTE.STAR_DETAIL, {
                star_name: starName,
                star_birth_year: birthYear || null
            }, false, "Error inserting star.");

            if (response.status === 200) {
                setMessage({ type: 'success', text: `Success! starID: ${response.data.new_star_id}` });
            }
        } catch (error) {
            setMessage({ type: 'error', text: 'Error adding star.' });
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div className="adder-container">
            <h1>Add New Star</h1>
            <form onSubmit={handleSubmit}>
                {message && (
                    <p className={`message ${message.type}`}>
                        {message.text}
                    </p>
                )}
                <div className="adder-input-group">
                    <input
                        type="text"
                        placeholder="Star Name"
                        value={starName}
                        onChange={(e) => setStarName(e.target.value)}
                        required
                    />
                    <input
                        type="number"
                        value={birthYear}
                        placeholder="Birth Year (optional)"
                        onChange={(e) => setBirthYear(e.target.value)}
                    />
                </div>
                <button type="adder-submit" disabled={isSubmitting}>
                    Submit
                </button>
            </form>
        </div>
    );
}

export default StarAdder;