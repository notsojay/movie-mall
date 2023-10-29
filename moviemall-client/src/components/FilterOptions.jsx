import {REQUEST_TYPE} from "../config/movieRequestTypes";
import React from "react";

export const FilterOptions = (props) => {
    const {
        recordsPerPage,
        requestType,
        setRecordsPerPage,
        setCurrentFirstSortKey,
        setCurrentFirstSortOrder,
        setCurrentSecondSortKey,
        setCurrentSecondSortOrder
    } = props;

    const initialSortValue = localStorage.getItem('sortValue') || 'rating-desc-title-asc';

    return requestType === REQUEST_TYPE.GET_TOP20_MOVIES
        ? null
        :(
            <div>
                <div>
                    <label htmlFor="records-per-page">Movies Per Page：</label>
                    <select
                        id="records-per-page"
                        value={recordsPerPage}
                        onChange={(e) => setRecordsPerPage(e.target.value)}
                    >
                        <option value="10" selected={recordsPerPage === "10"}>10</option>
                        <option value="25" selected={recordsPerPage === "25"}>25</option>
                        <option value="50" selected={recordsPerPage === "50"}>50</option>
                        <option value="100" selected={recordsPerPage === "100"}>100</option>
                    </select>
                </div>
                <div>
                    <label htmlFor="sort-order">Sort By：</label>
                    <select id="sort-order"
                            value={initialSortValue}
                            onChange={(e) => {
                                const value = e.target.value;
                                localStorage.setItem('sortValue', value);
                                const valueArray = value.split('-');
                                const firstSortKey = valueArray[0];    // "title"
                                const firstSortOrder = valueArray[1];  // "asc"
                                const secondSortKey = valueArray[2];   // "rating"
                                const secondSortOrder = valueArray[3]; // "asc"

                                setCurrentFirstSortKey(firstSortKey);
                                setCurrentFirstSortOrder(firstSortOrder);
                                setCurrentSecondSortKey(secondSortKey);
                                setCurrentSecondSortOrder(secondSortOrder);
                            }}
                    >
                        <option value="title-asc-rating-asc">Title: A-Z, Rating: Low-High</option>
                        <option value="title-asc-rating-desc">Title: A-Z, Rating: High-Low</option>
                        <option value="title-desc-rating-asc">Title: Z-A, Rating: Low-High</option>
                        <option value="title-desc-rating-desc">Title: Z-A, Rating: High-Low</option>
                        <option value="rating-asc-title-asc">Rating: Low-High, Title: A-Z</option>
                        <option value="rating-asc-title-desc">Rating: Low-High, Title: Z-A</option>
                        <option value="rating-desc-title-asc" selected>Rating: High-Low, Title: A-Z</option>
                        <option value="rating-desc-title-desc">Rating: High-Low, Title: Z-A</option>
                    </select>
                </div>
            </div>
        );
};