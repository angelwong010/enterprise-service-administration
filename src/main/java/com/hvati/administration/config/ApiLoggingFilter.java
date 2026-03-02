package com.hvati.administration.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Logs every API request at INFO and logs ERROR when the response is not successful (status >= 400).
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiLoggingFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String method = request.getMethod();
        String path = request.getRequestURI();
        if (request.getQueryString() != null) {
            path = path + "?" + request.getQueryString();
        }

        log.info("API called: {} {}", method, path);

        StatusCapturingResponseWrapper wrappedResponse = new StatusCapturingResponseWrapper(response);
        try {
            filterChain.doFilter(request, wrappedResponse);
        } finally {
            int status = wrappedResponse.getCapturedStatus();
            if (status >= 400) {
                log.error("API error: {} {} -> status={}", method, request.getRequestURI(), status);
            }
        }
    }

    /**
     * Wraps the response to capture the status code (setStatus/sendError are not visible on the original response after the chain).
     */
    private static class StatusCapturingResponseWrapper extends HttpServletResponseWrapper {
        private int capturedStatus = 200;

        public StatusCapturingResponseWrapper(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setStatus(int sc) {
            this.capturedStatus = sc;
            super.setStatus(sc);
        }

        @Override
        public void sendError(int sc) throws IOException {
            this.capturedStatus = sc;
            super.sendError(sc);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            this.capturedStatus = sc;
            super.sendError(sc, msg);
        }

        public int getCapturedStatus() {
            return capturedStatus;
        }
    }
}
