import {useAuth} from "../hooks/useAuth";
import useLogout from "../hooks/useLogout";
import {useState} from "react";
import {Link, useNavigate} from "react-router-dom";
import {APP_ROUTES} from "../config/appRoutes";
import {REQUEST_TYPE} from "../config/movieRequestTypes";
import "../assets/styles/employee-navbar.css"

function EmployeeNavbar() {
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

    if (isChecking || showLoginModal) return null;


    return (
        <div className="employee-navbar">
            <Link className="transparent-custom-button" to={APP_ROUTES.EMPLOYEE_DASHBOARD}>Dashboard</Link>
            <Link className="transparent-custom-button" to={APP_ROUTES.MOVIE_ADDER}>Add Movie</Link>
            <Link className="transparent-custom-button" to={APP_ROUTES.STAR_ADDER}>Add star</Link>
            {isLoggedIn && (
                <Link className="transparent-custom-button" onClick={(e) => { e.preventDefault(); handleLogOut(); }} to="#">
                    Log out
                </Link>
            )}
        </div>
    );
}

export default EmployeeNavbar;