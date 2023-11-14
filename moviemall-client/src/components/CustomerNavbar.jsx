import {useState} from 'react';
import {Link, useNavigate} from 'react-router-dom';
import LoginForm from "./LoginForm";
import useLogout from '../hooks/useLogout';
import {useAuth} from "../hooks/useAuth";

import '../assets/styles/customer-navbar.css';
import {REQUEST_TYPE} from "../config/movieRequestTypes";
import {APP_ROUTES} from "../config/appRoutes";

function CustomerNavbar() {
    const { isLoggedIn, isChecking, showLoginModal, setShowLoginModal } = useAuth();
    const handleLogOut = useLogout();
    // const [showLoginModal, setShowLoginModal] = useState(false);
    const [searchParams, setSearchParams] = useState({
        title: '',
        year: '',
        director: '',
        starName: '',
    });
    const navigate = useNavigate();

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setSearchParams({ ...searchParams, [name]: value });
    };

    const handleSearch = () => {
        const query = new URLSearchParams();
        Object.entries(searchParams).forEach(([key, value]) => {
            if (value) query.append(key, value);
        });
        navigate(`${APP_ROUTES.MOVIE_LIST}?requestType=${REQUEST_TYPE.SEARCH_MOVIES}&${query.toString()}`);
        setSearchParams({
            title: '',
            year: '',
            director: '',
            starName: ''
        });
    };

    if (isChecking || showLoginModal) return null;

    return (
        <div className="navbar">
            <div className="navbar-section navbar-left">
                <Link className="transparent-custom-button" to={APP_ROUTES.HOME}>Home</Link>
                <Link className="transparent-custom-button" to={`${APP_ROUTES.MOVIE_LIST}?requestType=${REQUEST_TYPE.GET_TOP20_MOVIES}`}>Top 20 Movies</Link>
            </div>
            <div className="navbar-section navbar-center">
                <div className="search-bar">
                    <input
                        type="text"
                        placeholder="Title"
                        name="title"
                        value={searchParams.title}
                        onChange={handleInputChange}
                    />
                    <input
                        className="year"
                        type="number"
                        placeholder="Year"
                        name="year"
                        value={searchParams.year}
                        onChange={handleInputChange}
                    />
                    <input
                        type="text"
                        placeholder="Director"
                        name="director"
                        value={searchParams.director}
                        onChange={handleInputChange}
                    />
                    <input
                        type="text"
                        placeholder="Star's Name"
                        name="starName"
                        value={searchParams.starName}
                        onChange={handleInputChange}
                    />
                    <Link className="transparent-custom-button" onClick={(e) => { e.preventDefault(); handleSearch(); }} to="#">
                        Search
                    </Link>
                </div>
            </div>
            <div className="navbar-section navbar-right">
                <Link className="transparent-custom-button" to={APP_ROUTES.SHOPPING_CART}>Checkout</Link>
                {isLoggedIn ? (
                    <Link className="transparent-custom-button" onClick={(e) => { e.preventDefault(); handleLogOut(); }} to="#">
                        Log out
                    </Link>
                ) : (
                    <>
                        <Link className="transparent-custom-button" onClick={(e) => { e.preventDefault(); setShowLoginModal(true); }} to="#">
                            Log in
                        </Link>
                        {/*{showLoginModal && <LoginForm onClose={() => setShowLoginModal(false)} userTpye="customer" />}*/}
                    </>
                )}
            </div>
        </div>
    );
}

export default CustomerNavbar;