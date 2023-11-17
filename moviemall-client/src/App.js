import './App.css';
import MovieList from './views/MovieList';
import MovieDetail from './views/MovieDetail';
import StarDetail from './views/StarDetail';
import CustomerNavbar from './components/CustomerNavbar'
import {HashRouter, Navigate, Outlet, Route, Routes, useLocation} from 'react-router-dom';
import {AuthProvider} from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute"
import React, {useContext, useEffect} from "react";
import Home from "./views/Home";
import {APP_ROUTES} from "./config/appRoutes";
import ShoppingCart from "./views/ShoppingCart"
import SubscribeSection from "./components/SubscribeSection";
import EmployeeDashboard from "./views/EmployeeDashboard";
import EmployeeNavbar from "./components/EmployeeNavbar";
import StarAdder from "./views/StarAdder";
import MovieAdder from "./views/MovieAdder";

function App() {
    useEffect(() => {
        window.addEventListener('error', e => {
            if (e.message === 'ResizeObserver loop limit exceeded') {
                const resizeObserverErrDiv = document.getElementById(
                    'webpack-dev-server-client-overlay-div'
                );
                const resizeObserverErr = document.getElementById(
                    'webpack-dev-server-client-overlay'
                );
                if (resizeObserverErr) {
                    resizeObserverErr.setAttribute('style', 'display: none');
                }
                if (resizeObserverErrDiv) {
                    resizeObserverErrDiv.setAttribute('style', 'display: none');
                }
            }
        });
    }, []);

    return (
        <HashRouter>
            <div className="body-container">
                <Routes>

                    <Route element={<LayoutWithEmployee />}>
                        <Route path={APP_ROUTES.EMPLOYEE_DASHBOARD} element={<ProtectedRoute userType="employee" />}>
                            <Route index element={<EmployeeDashboard />} />
                            <Route path={APP_ROUTES.MOVIE_ADDER} element={<MovieAdder />} />
                            <Route path={APP_ROUTES.STAR_ADDER} element={<StarAdder />} />
                        </Route>
                    </Route>

                    <Route element={<LayoutWithCustomer />}>
                        <Route path={APP_ROUTES.HOME} element={<ProtectedRoute userType="customer" />}>
                            <Route index element={<Home />} />
                            <Route path={APP_ROUTES.MOVIE_LIST} element={<MovieList />} />
                            <Route path={APP_ROUTES.MOVIE_DETAIL} element={<MovieDetail />} />
                            <Route path={APP_ROUTES.STAR_DETAIL} element={<StarDetail />} />
                            <Route path={APP_ROUTES.SHOPPING_CART} element={<ShoppingCart />} />
                        </Route>
                        <Route path="*" element={<Navigate to="/" replace />} />
                    </Route>

                </Routes>
            </div>
            <SubscribeSection />
        </HashRouter>
    );

}

function LayoutWithEmployee() {
    return (
        <AuthProvider>
            <EmployeeNavbar />
            <Outlet />
        </AuthProvider>
    );
}

function LayoutWithCustomer() {
    return (
        <AuthProvider>
            <CustomerNavbar />
            <Outlet />
        </AuthProvider>
    );
}

export default App;
