package com.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

//@WebFilter("/*")
//public class CORSFilter implements Filter {
//
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
//            throws IOException, ServletException {
//        HttpServletResponse response = (HttpServletResponse) res;
//
//        response.setHeader("Access-Control-Allow-Origin", "http://movie-mall.com:8080");
//        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
//
//        chain.doFilter(req, res);
//    }
//
//    public void init(FilterConfig filterConfig) {}
//
//    public void destroy() {}
//
//}
