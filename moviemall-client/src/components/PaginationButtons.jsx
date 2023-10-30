import {REQUEST_TYPE} from "../config/movieRequestTypes";
import React from "react";

import '../assets/styles/pagination.css'

export const PaginationButtons = (props) => {
    const { requestType, currentPage, totalPages, updateSetting } = props;
    let stepCount = 0;

    const changePage = (newPage) => {
        if (newPage > 0 && newPage <= totalPages && newPage !== currentPage) {
            updateSetting({ currentPage: newPage });
        }
    };

    const getProgressBarWidth = () => {
        let relativePage = currentPage % 5;
        if (currentPage >= 4 && relativePage >= 0) relativePage = 3;
        return `${((relativePage - 1) / (stepCount - 1)) * 100}%`;
    };

    const shouldDisplay = (pageNumber) => {
        if (totalPages === 1) {
            return null;
        }
        if (currentPage <= 2) {
            return pageNumber <= 4 || pageNumber === totalPages;
        }
        else {
            const maxDistance = 1;
            return Math.abs(pageNumber - currentPage) <= maxDistance || pageNumber === 1 || pageNumber === totalPages;
        }
    };

    const PageButton = ({ pageNumber }) => (
        <button
            onClick={() => changePage(pageNumber)}
            className={`circle ${currentPage === pageNumber ? 'active' : ''}`}
        >
            {pageNumber}
        </button>
    );

    return (
        <div className="pagination-body">
            <div className="pagination-container">
                <div className="steps">
                    {Array.from({ length: totalPages }).map((_, index) => {
                        const pageNumber = index + 1;
                        if (shouldDisplay(pageNumber)) {
                            ++stepCount;
                            return <PageButton key={pageNumber} pageNumber={pageNumber} />;
                        }
                        return null;
                    })}
                    <div
                        className="progress-bar"
                        style={{
                            width: totalPages === 1 ? 0 : `${100}%`
                        }}>
                        <span
                            className="indicator"
                            style={{
                                width: getProgressBarWidth()
                            }}
                        ></span>
                    </div>
                </div>
                <div className="buttons">
                    <>
                        <button
                            className="page-custom-button"
                            onClick={() => changePage(currentPage - 1)}
                            disabled={currentPage === 1}
                        >
                            Prev
                        </button>
                        <button
                            className="page-custom-button"
                            onClick={() => changePage(currentPage + 1)}
                            disabled={currentPage === totalPages}
                        >
                            Next
                        </button>
                    </>
                </div>
            </div>
        </div>
    );
};
