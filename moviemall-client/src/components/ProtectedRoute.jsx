import React, {useEffect} from "react";
import {useAuth} from "../hooks/useAuth";
import {Outlet, useLocation, useNavigate} from "react-router-dom";
import LoginForm from "./LoginForm";

import '../assets/styles/table.css';
import {APP_ROUTES} from "../config/appRoutes";

function ProtectedRoute() {
    const { isLoggedIn, showLoginModal, setShowLoginModal, isChecking } = useAuth();
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        if (isChecking) return;

        if (!isLoggedIn) {
            if (location.pathname !== APP_ROUTES.HOME) {
                setShowLoginModal(true);
                navigate(APP_ROUTES.HOME, { replace: true, state: { from: location } });  // redirect to home but remember the previous location
            }
        }
    }, [isLoggedIn, location, navigate, setShowLoginModal]);

    if (isChecking) {
        return null;
    }

    return (
        <>
            <Outlet />
            {showLoginModal && <LoginForm isOpen={showLoginModal} onClose={() => setShowLoginModal(false)} />}
        </>
    );
}

export default ProtectedRoute;