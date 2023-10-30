import React, {useEffect, useState} from 'react';
import useLogin from '../hooks/useLogin';
import useInputAnimation from '../hooks/useInputAnimation';

import '../assets/styles/login-form.css'

function LoginForm({ onClose }) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [login, loginResponse] = useLogin();
    const [animateInput, setIsActive, isActive] = useInputAnimation();
    const [error, setError] = useState(null);

    useEffect(() => {
        if (loginResponse && loginResponse === "success") {
            onClose();
        }
    }, [loginResponse, onClose]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!email.trim() || !password.trim()) {
            setError('Please enter an Email and Password');
            setIsActive(false);
            return;
        }
        const errorMessage = await login(email, password);
        if (errorMessage) {
            setError(errorMessage);
            setIsActive(false);
        }
    };

    return (
        <div className="modalBackground">
            <div className="center">
                <form onSubmit={handleSubmit}>
                    <button className="closeModalX" onClick={onClose}>X</button>
                    <div className="title">Login</div>
                    {error && <div className="error-message">{error}</div>}
                    <span className="inputs">
                            <span className="inputf">
                                <input
                                    type="email"
                                    className={`input ${error ? "error-shake" : ""}`}
                                    placeholder="Email"
                                    value={email}
                                    onChange={e => setEmail(e.target.value)}
                                />
                                <span className="label">Email</span>
                                {/*<span className="material-icons icon">email</span>*/}
                            </span>
                            <span className="inputf">
                                <input
                                    type="password"
                                    className={`input ${error ? "error-shake" : ""}`}
                                    placeholder="Password"
                                    value={password}
                                    onChange={e => setPassword(e.target.value)}
                                />
                                <span className="label">Password</span>
                                {/*<span className="material-icons icon">lock</span>*/}
                            </span>
                        </span>
                    <div className="links">
                        <a className="transparent-custom-button" href="#">Forgot Password</a>
                        <label htmlFor="remember">
                            <input type="checkbox" id="remember" />
                            Remember Me
                        </label>
                    </div>
                    <button
                        type="submit"
                        className={`btn transparent-custom-button ${isActive ? "active" : ""}`}
                        onClick={animateInput}
                    >
                        <span>Login</span>
                        <div className="dots">
                            <div className={`dot ${error ? "no-animation" : ""}`} style={{ "--delay": "0s" }}></div>
                            <div className={`dot ${error ? "no-animation" : ""}`} style={{ "--delay": "0.5s" }}></div>
                            <div className={`dot ${error ? "no-animation" : ""}`} style={{ "--delay": "1s" }}></div>
                        </div>
                    </button>
                    <div className="text">
                        New user? Create an account <a href="#">Register</a>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default LoginForm;
