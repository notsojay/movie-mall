import {useContext} from 'react';
import {AuthContext} from '../context/AuthContext';
import {SERVLET_ROUTE} from '../config/servletRoutes';
import axios from "axios";

function useLogout() {
    const { setIsLoggedIn } = useContext(AuthContext);

    return async () => {
        try {
            const response = await axios.delete(SERVLET_ROUTE.AUTHENTICATION, {
                headers: {
                    'Accept': 'application/json'
                }
            });
            if (response.status === 200) {
                setIsLoggedIn(false);
            } else {
                console.error('Failed to log out:', response.data.message);
            }
        } catch (err) {
            console.error('Error:', err);
        }
    };
}

export default useLogout;
