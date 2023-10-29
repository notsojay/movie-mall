import {REQUEST_TYPE} from "../config/movieRequestTypes";
import React from "react";
import {APP_ROUTES} from "../config/appRoutes";

export const PaginationButtons = (props) => {
    const { prevPage, nextPage, currentPage, totalPages } = props;

    // 判断是否显示当前页码按钮
    const shouldDisplay = (pageNumber) => {
        return pageNumber === 1 ||
            pageNumber === totalPages ||
            pageNumber === currentPage ||
            pageNumber === currentPage - 1 ||
            pageNumber === currentPage + 1;
    };

    return (
        <>
            <button onClick={() => prevPage()} disabled={currentPage === 1}>Prev</button>

            {Array.from({ length: totalPages }).map((_, index) => {
                const pageNumber = index + 1;

                if (shouldDisplay(pageNumber)) {
                    return (
                        <button
                            key={pageNumber}
                            onClick={() => {/* 这里可以添加跳转到特定页码的逻辑 */}}
                            className={currentPage === pageNumber ? 'active' : ''}
                        >
                            {pageNumber}
                        </button>
                    );
                } else if (pageNumber === 2 && currentPage > 3) {
                    return <span key={pageNumber}>...</span>;
                } else if (pageNumber === totalPages - 1 && currentPage < totalPages - 2) {
                    return <span key={pageNumber}>...</span>;
                }
                return null;
            })}

            <button onClick={() => nextPage()} disabled={currentPage === totalPages}>Next</button>
        </>
    );
};
