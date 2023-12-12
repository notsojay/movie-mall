package com.filters;

import com.utils.LogUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.logging.Logger;

@WebFilter("/*")
public class PerformanceFilter implements Filter {
    private final Logger logger = LogUtil.getLogger();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestUri = httpRequest.getRequestURI();
        String httpMethod = httpRequest.getMethod();

        long startTime = System.nanoTime();

        chain.doFilter(request, response);

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;

        logger.info(httpMethod + " " + requestUri + " - Servlet Time: " + elapsedTime + "ns");
    }

    @Override
    public void destroy() {

    }
}
