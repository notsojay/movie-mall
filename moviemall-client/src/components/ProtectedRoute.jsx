import React, {useEffect} from "react";
import {useAuth} from "../hooks/useAuth";
import {Outlet, useLocation, useNavigate} from "react-router-dom";
import LoginForm from "./LoginForm";

import '../assets/styles/table.css';
import {APP_ROUTES} from "../config/appRoutes";

function ProtectedRoute({ userType }) {
    const { isLoggedIn, showLoginModal, setShowLoginModal, isChecking } = useAuth();
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        if (isChecking) return;

        if (!isLoggedIn) {
            const targetRoute = userType === 'employee' ? APP_ROUTES.EMPLOYEE_DASHBOARD : APP_ROUTES.HOME;
            const isEmployeeDashboardOrSubRoute = location.pathname.startsWith(APP_ROUTES.EMPLOYEE_DASHBOARD);

            if (location.pathname !== APP_ROUTES.HOME || isEmployeeDashboardOrSubRoute) {
                navigate(targetRoute, { replace: true, state: { from: location } });
            }

            if (location.pathname !== APP_ROUTES.HOME || location.pathname.startsWith(APP_ROUTES.EMPLOYEE_DASHBOARD)) {
                setShowLoginModal(true);
            }
        }
    }, [isLoggedIn, location, navigate, setShowLoginModal]);

    if (isChecking) {
        return null;
    }

    return (
        <>
            <Outlet />
            { showLoginModal && <LoginForm onClose={() => setShowLoginModal(false)} userType={userType} />}
        </>
    );
}

export default ProtectedRoute;