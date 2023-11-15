import React, {useEffect, useRef, useState} from 'react';
import useLogin from '../hooks/useLogin';
import useInputAnimation from '../hooks/useInputAnimation';
import ReCAPTCHA from 'react-google-recaptcha';

import '../assets/styles/login-form.css'
import {useAuth} from "../hooks/useAuth";

function LoginForm({ onClose, userType }) {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [login, loginResponse] = useLogin();
    const [captchaValue, setCaptchaValue] = useState(null);
    const [animateInput, isAnimationActive, setIsAnimationActive] = useInputAnimation();
    const [error, setError] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const recaptchaRef = useRef(null);

    const resetCaptcha = () => {
        if (recaptchaRef.current) {
            recaptchaRef.current.reset();
        }
        setCaptchaValue(null);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);

        if (!email.trim() || !password.trim()) {
            setError('Please enter an Email and Password.');
            setIsAnimationActive(false);
            return;
        }
        if (!captchaValue) {
            setError('Please verify that you are not a robot.');
            setIsAnimationActive(false);
            return;
        }
        const isErrorMessage = await login(email, password, captchaValue, userType);
        console.log(isErrorMessage);
        if (isErrorMessage) {
            setError(isErrorMessage);
            setIsAnimationActive(false);
            resetCaptcha();
        } else {
            setEmail('');
            setPassword('');
            setCaptchaValue(null);
            resetCaptcha();
        }
        setIsLoading(false);
    };
    const handleCaptchaChange = (value) => {
        setCaptchaValue(value);
    };

    useEffect(() => {
        if (loginResponse && loginResponse === "success") {
            onClose();
        }
    }, [loginResponse, onClose]);

    return (
        <div className="modalBackground">
            <div className="center">
                <form className={"login-form"} onSubmit={handleSubmit}>
                    {userType === "customer" && <button className="closeModalX" onClick={onClose}>X</button>}
                    <div className="title">{userType === 'customer' ? "Login" : "Employee Login"}</div>
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
                    <ReCAPTCHA
                        ref={recaptchaRef}
                        sitekey={process.env.REACT_APP_RECAPTCHA_SITE_KEY}
                        onChange={handleCaptchaChange}
                        className="login-recaptcha"
                        theme="dark"
                    />
                    <button
                        type="submit"
                        className={`btn ${isAnimationActive ? "active" : ""}`}
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
