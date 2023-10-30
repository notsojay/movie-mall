import './App.css';
import MovieList from './views/MovieList';
import MovieDetail from './views/MovieDetail';
import StarDetail from './views/StarDetail';
import Navbar from './components/Navbar'
import {HashRouter, Navigate, Route, Routes} from 'react-router-dom';
import {AuthProvider} from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute"
import React from "react";
import Home from "./views/Home";
import {APP_ROUTES} from "./config/appRoutes";
import ShoppingCart from "./views/ShoppingCart";
import Payment from "./views/Payment";

function App() {
    return (
        <div className="page">
            <HashRouter>
                <AuthProvider>
                    <Navbar />
                    <Routes>
                        <Route path={APP_ROUTES.HOME} element={<ProtectedRoute />}>
                            <Route index element={<Home />} />
                            <Route path={APP_ROUTES.MOVIE_LIST} element={<MovieList />} />
                            <Route path={APP_ROUTES.MOVIE_DETAIL} element={<MovieDetail />} />
                            <Route path={APP_ROUTES.STAR_DETAIL} element={<StarDetail />} />
                            <Route path={APP_ROUTES.SHOPPING_CART} element={<ShoppingCart />} />
                            <Route path={APP_ROUTES.PAYMENT} element={<Payment />} />
                        </Route>
                        <Route path="*" element={<Navigate to="/" replace />} />
                    </Routes>
                </AuthProvider>
            </HashRouter>
        </div>
    );
}




export default App;
