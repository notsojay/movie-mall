import {useContext} from 'react';
import {AuthContext} from '../context/AuthContext';
import {API_PATH} from '../config/servletPaths';
import axios from "axios";

function useLogout() {
    const { setIsLoggedIn } = useContext(AuthContext);

    return async () => {
        try {
            const response = await axios.delete(API_PATH.AUTHENTICATION, {
                headers: {
                    'Accept': 'application/json'
                }
            });
            if (response.data.status === "success") {
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
