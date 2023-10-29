import {useState} from 'react';
import {API_PATH} from "../config/servletPaths";
import {useAuth} from "./useAuth";
import axios from "axios";

function useLogin() {
    const [loginResponse, setLoginResponse] = useState(null);
    const { setIsLoggedIn } = useAuth();

    const login = async (email, password) => {
        try {
            const response = await axios.post(API_PATH.AUTHENTICATION, {
                    email, password
                }, {
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
            });
            setLoginResponse(response.data.status);
            if (response.data.status !== 'success') {
                return response.data.message;
            } else if (response.data.status === 'success') {
                setIsLoggedIn(true);
                return "";
            }

        } catch (err) {
            console.error('Error:', err.response ? err.response.data.message : err.message);
            return "An error occurred while trying to log in.";
        }
    };

    return [login, loginResponse];
}

export default useLogin;
