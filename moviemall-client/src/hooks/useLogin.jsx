import {useState} from 'react';
import {SERVLET_ROUTE} from "../config/servletRoutes";
import {useAuth} from "./useAuth";
import {postData} from "../utils/apiCaller";

function useLogin() {
    const [loginResponse, setLoginResponse] = useState(null);
    const { setIsLoggedIn } = useAuth();

    const login = async (email, password, captchaValue, userType='customer') => {
        try {
            const response = await postData(SERVLET_ROUTE.AUTHENTICATION, {
                email: email,
                password: password,
                captchaValue: captchaValue,
                userType: userType
            }, false, "An error occurred while trying to log in.");

            setLoginResponse(response.data.status);

            if (response.data.status !== 'success') {
                return response.data.message;
            } else if (response.data.status === 'success') {
                setIsLoggedIn(true);
                return "";
            }
        } catch (err) {
            console.error('Error: ', err.response ? err.response?.message : err.message);
        }
    };

    return [login, loginResponse];
}

export default useLogin;
