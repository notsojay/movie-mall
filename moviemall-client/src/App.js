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

function App() {
    return (
        <HashRouter>
            <AuthProvider>
                <Navbar />
                <Routes>
                    <Route path={APP_ROUTES.HOME} element={<ProtectedRoute />}>
                        <Route index element={<Home />} />
                        <Route path={APP_ROUTES.MOVIE_LIST} element={<MovieList />} />
                        <Route path={APP_ROUTES.MOVIE_DETAIL} element={<MovieDetail />} />
                        <Route path={APP_ROUTES.STAR_DETAIL} element={<StarDetail />} />
                    </Route>
                    <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
            </AuthProvider>
        </HashRouter>
    );
}




export default App;
