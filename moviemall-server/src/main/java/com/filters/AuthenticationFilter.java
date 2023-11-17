package com.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        if (httpRequest.getScheme().equals("http")) {
            String redirectURL = "https://movie-mall.com:8443" + httpRequest.getRequestURI();
            httpResponse.sendRedirect(redirectURL);
            return;
        }

        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String homeURI = contextPath + "/";

        boolean loggedIn = session != null && session.getAttribute("isLoggedIn") != null && (boolean) session.getAttribute("isLoggedIn");
        boolean isHomeRequest = requestURI.equals(homeURI);
        boolean isAuthServletRequest = requestURI.startsWith(contextPath + "/AuthenticationServlet");
        boolean isGetAllGenresRequest = "get-all-genres".equals(httpRequest.getParameter("requestType")) && requestURI.endsWith("/MovieListServlet");

        if (loggedIn || isHomeRequest || isAuthServletRequest || isGetAllGenresRequest) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect(homeURI);
        }
    }

    @Override
    public void destroy() {

    }
}
