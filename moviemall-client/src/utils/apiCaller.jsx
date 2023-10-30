import axios from 'axios';
import {API_PATH} from "../config/servletPaths";

export const fetchData = async (endpoint, params, includeCookies = false, errorDescription = "Error fetching data") => {
    try {
        console.log(`Sending request to: ${endpoint} with parameters:`, params);
        const response = await axios.get(endpoint, {
            headers: { 'Accept': 'application/json' },
            params: params,
            withCredentials: includeCookies
        });
        return response.data;
    } catch (error) {
        console.error(`${errorDescription}:`, error);
        throw error;
    }
};

export const postData = async (endpoint, data, includeCookies = false, errorDescription = "Error posting data") => {
    try {
        console.log(`Sending POST request to: ${endpoint} with data:`, data);
        const response = await axios.post(endpoint, data, {
            headers: { 'Content-Type': 'application/json' },
            withCredentials: includeCookies
        });
        return response.data;
    } catch (error) {
        console.error(`${errorDescription}:`, error);
        throw error;
    }
};


export const addToCart = async (movieId, movieTitle, moviePrice) => {
    try {
        const response = await postData(API_PATH.SHOPPING_CART, {
            movieId: movieId,
            movieTitle: movieTitle,
            moviePrice: moviePrice,
            quantity: 1
        }, false, 'Error adding movie to cart');

        if (response && response.status === 'success') {
            console.log('Movie added to cart successfully');
        } else {
            console.error('Failed to add movie to cart');
        }
    } catch (error) {
        console.error('Error adding movie to cart', error);
    }
};