import {useState} from 'react';
import {Link} from 'react-router-dom';
import LoginForm from "./LoginForm";
import useLogout from '../hooks/useLogout';
import {useAuth} from "../hooks/useAuth";

import '../assets/styles/navbar.css';
import '../assets/styles/custom-button.css'
import {REQUEST_TYPE} from "../config/movieRequestTypes";
import {APP_ROUTES} from "../config/appRoutes";

function Navbar() {
    const { isLoggedIn, isChecking } = useAuth();
    const handleLogOut = useLogout();
    const [showLoginModal, setShowLoginModal] = useState(false);

    if (isChecking) return null;

    return (
        <div className="navbar">
            <Link className="custom-button" to={APP_ROUTES.HOME}>Home</Link>
            <Link className="custom-button" to={`${APP_ROUTES.MOVIE_LIST}?requestType=${REQUEST_TYPE.GET_TOP20_MOVIES}`}>Top 20 Movies</Link>
            {isLoggedIn ? (
                <Link className="custom-button" onClick={(e) => { e.preventDefault(); handleLogOut(); }} to="#">
                    Log out
                </Link>
            ) : (
                <>
                    <Link className="custom-button" onClick={(e) => { e.preventDefault(); setShowLoginModal(true); }} to="#">
                        Log in
                    </Link>
                    {showLoginModal && <LoginForm isOpen={showLoginModal} onClose={() => setShowLoginModal(false)} />}
                </>
            )}
        </div>
    );
}

export default Navbar;