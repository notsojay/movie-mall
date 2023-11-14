import React, {createContext, useState, useEffect} from 'react';
import {SERVLET_ROUTE} from "../config/servletRoutes";
import axios from "axios";

import '../assets/styles/loading.css';

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [isLoggedIn, setIsLoggedIn] = useState(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showLoginModal, setShowLoginModal] = useState(false);
    const [isChecking, setIsChecking] = useState(true);

    useEffect(() => {
        const fetchSessionStatus = async () => {
            try {
                setIsChecking(true);
                const response = await axios.get(SERVLET_ROUTE.AUTHENTICATION, {
                    headers: {
                        'Accept': 'application/json'
                    }
                });
                if (response.status === 200 && response.data.status === "logged-in") {
                    setIsLoggedIn(true);
                    setShowLoginModal(false);
                }
            } catch (err) {
                console.error('Error fetching session status:', err);
                setError(err.message || 'An error occurred while checking session status.');
            } finally {
                setIsLoading(false);
                setIsChecking(false);
            }
        };
        fetchSessionStatus().catch(console.error);
    }, []);

    if (error) {
        return <p>Error: {error}</p>;
    }

    return (
        <AuthContext.Provider value={{ isLoggedIn, setIsLoggedIn, showLoginModal, setShowLoginModal, isLoading, isChecking }}>
            {children}
        </AuthContext.Provider>
    );
};
