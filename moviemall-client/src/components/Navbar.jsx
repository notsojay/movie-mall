import '../assets/styles/navbar.css'
import {Link} from "react-router-dom";

function Navbar() {
    return (
        <div className="navbar">
            <Link to="/movie-mall">Home</Link>
        </div>
    );
}

export default Navbar;