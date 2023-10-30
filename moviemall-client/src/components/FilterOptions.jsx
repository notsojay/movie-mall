import {REQUEST_TYPE} from "../config/movieRequestTypes";
import React from "react";
import '../assets/styles/filter-options.css'

export const FilterOptions = (props) => {
    const {
        recordsPerPage,
        requestType,
        initialSortValue,
        updateSetting
    } = props;

    return requestType === REQUEST_TYPE.GET_TOP20_MOVIES
        ? null
        :(
            <div className="filter-options-container">
                <div className="filter-option">
                    <label className="filter-label" htmlFor="records-per-page">Movies Per Page：</label>
                    <select
                        id="records-per-page"
                        value={recordsPerPage}
                        onChange={(e) => updateSetting({recordsPerPage: e.target.value})}
                    >
                        <option value="10">10</option>
                        <option value="25">25</option>
                        <option value="50">50</option>
                        <option value="100">100</option>
                    </select>
                </div>
                <div className="filter-option">
                    <label className="filter-label" htmlFor="sort-order">Sort By：</label>
                    <select id="sort-order"
                            value={initialSortValue}
                            onChange={(e) => {
                                const value = e.target.value;
                                updateSetting({initialSortValue: value});
                            }}
                    >
                        <option value="title-asc-rating-asc">Title: A-Z, Rating: Low-High</option>
                        <option value="title-asc-rating-desc">Title: A-Z, Rating: High-Low</option>
                        <option value="title-desc-rating-asc">Title: Z-A, Rating: Low-High</option><option value="title-desc-rating-desc">Title: Z-A, Rating: High-Low</option>
                        <option value="rating-asc-title-asc">Rating: Low-High, Title: A-Z</option>
                        <option value="rating-asc-title-desc">Rating: Low-High, Title: Z-A</option>
                        <option value="rating-desc-title-asc">Rating: High-Low, Title: A-Z</option>
                        <option value="rating-desc-title-desc">Rating: High-Low, Title: Z-A</option>
                    </select>
                </div>
            </div>
        );
};