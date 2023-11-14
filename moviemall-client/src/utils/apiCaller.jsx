import axios from 'axios';
import {SERVLET_ROUTE} from "../config/servletRoutes";

export const fetchData = async (endpoint, params, includeCookies = false, errorDescription = "Error fetching data") => {
    try {
        console.log(`Sending request to: ${endpoint} with parameters:`, params);
        const response = await axios.get(endpoint, {
            headers: { 'Accept': 'application/json' },
            params: params,
            withCredentials: includeCookies
        });
        return response;
    } catch (error) {
        console.error(`${errorDescription}:`, error);
        throw error;
    }
};

export const postData = async (endpoint, data, includeCookies = false, errorDescription = "Error posting data") => {
    try {
        console.log(`Sending POST request to: ${endpoint} with data:`, data);
        const response = await axios.post(endpoint, data, {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            withCredentials: includeCookies
        });
        return response;
    } catch (error) {
        console.error(`${errorDescription}:`, error);
        throw error;
    }
};

export const addToCart = async (movieId, movieTitle, moviePrice) => {
    try {
        const response = await postData(SERVLET_ROUTE.SHOPPING_CART, {
            movieId: movieId,
            movieTitle: movieTitle,
            moviePrice: moviePrice,
            quantity: 1
        }, false, 'Error adding movie to cart');

        if (response.status === 200) {
            console.log('Movie updated to cart successfully');
        } else {
            console.error('Failed to add movie to cart');
        }
    } catch (error) {
        console.error('Error adding movie to cart', error);
    }
};
