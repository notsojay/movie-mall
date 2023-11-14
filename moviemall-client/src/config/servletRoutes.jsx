const BASE_URL = '/server'
// const BASE_URL = '/moviemall-server';

export const SERVLET_ROUTE = {
    AUTHENTICATION: `${BASE_URL}/AuthenticationServlet`,
    MOVIE_LIST: `${BASE_URL}/MovieListServlet`,
    MOVIE_DETAIL: `${BASE_URL}/MovieDetailServlet`,
    STAR_DETAIL: `${BASE_URL}/StarDetailServlet`,
    SHOPPING_CART: `${BASE_URL}/ShoppingCartServlet`,
    ORDER: `${BASE_URL}/OrderServlet`,
    EMPLOYEE: `${BASE_URL}/EmployeeServlet`
};
