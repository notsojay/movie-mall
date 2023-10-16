import './App.css';
import MovieList from './views/MovieList';
import MovieDetail from './views/MovieDetail';
import StarDetail from './views/StarDetail';
import Navbar from './components/Navbar'
import {HashRouter, Navigate, Route, Routes} from 'react-router-dom';

function App() {
    return (
        <HashRouter>
            <Navbar />
            <Routes>
                <Route path="/movie-mall" element={<MovieList />} />
                <Route path="/movie-detail" element={<MovieDetail />} />
                <Route path="/star-detail" element={<StarDetail />} />
                <Route path="*" element={<Navigate to="/movie-mall" replace />} />
            </Routes>
        </HashRouter>
    );
}

export default App;
