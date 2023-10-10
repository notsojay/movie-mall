import './App.css';
import MovieList from './views/MovieList';
import MovieDetail from './views/MovieDetail';
import StarDetail from './views/StarDetail';
import {BrowserRouter as Router, Navigate, Route, Routes} from 'react-router-dom';

function App() {
  return (
      <Router>
        <Routes>
            <Route path="/movie-mall" element={<MovieList />} />
            <Route path="/movie-detail" element={<MovieDetail />} />
            <Route path="/star-detail" element={<StarDetail />} />

            {/* Catch all route */}
            <Route path="*" element={<Navigate to="/movie-mall" replace />} />
        </Routes>
      </Router>
  );
}

export default App;
