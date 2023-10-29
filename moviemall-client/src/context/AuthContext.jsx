import React, {createContext, useState, useEffect} from 'react';
import {API_PATH} from "../config/servletPaths";
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
                const response = await axios.get(API_PATH.AUTHENTICATION, {
                    headers: {
                        'Accept': 'application/json'
                    }
                });
                if (response.data.status === "logged-in") {
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

    // useEffect(() => {
    //     setIsChecking(true);
    //
    //     fetchData(API_PATH.AUTHENTICATION, null, "Error fetching session status")
    //         .then(data => {
    //             if (data.status === "logged-in") {
    //                 setIsLoggedIn(true);
    //                 setShowLoginModal(false);
    //             }
    //         })
    //         .catch(err => {
    //             setError(err.message || 'An error occurred while checking session status.');
    //         })
    //         .finally(() => {
    //             setIsLoading(false);
    //             setIsChecking(false);
    //         });
    // }, []);


    if (error) {
        return <p>Error: {error}</p>;
    }

    return (
        <AuthContext.Provider value={{ isLoggedIn, setIsLoggedIn, showLoginModal, setShowLoginModal, isLoading, isChecking }}>
            {children}
        </AuthContext.Provider>
    );
};
