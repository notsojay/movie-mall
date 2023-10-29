import axios from 'axios';

export const fetchData = async (endpoint, params, errorDescription = "Error fetching data") => {
    try {
        console.log(`Sending request to: ${endpoint} with parameters:`, params);
        const response = await axios.get(endpoint, {
            headers: { 'Accept': 'application/json' },
            params: params
        });
        return response.data;
    } catch (error) {
        console.error(`${errorDescription}:`, error);
        throw error;
    }
};
