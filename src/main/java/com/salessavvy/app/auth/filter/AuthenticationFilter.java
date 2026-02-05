package com.salessavvy.app.auth.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.salessavvy.app.common.entity.Role;
import com.salessavvy.app.common.entity.User;
import com.salessavvy.app.user.repository.UserRepository;
import com.salessavvy.app.user.service.AuthServiceContract;

@WebFilter(urlPatterns = {"/api/*", "/admin/*"})
@Component
public class AuthenticationFilter implements Filter {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthenticationFilter.class);

    private final AuthServiceContract authService;
    private final UserRepository userRepository;

    private static final String ALLOWED_ORIGIN = "http://localhost:3000";

    private static final String[] UNAUTHENTICATED_PATHS = {
            "/api/users/register",
            "/api/auth/login"
    };

    public AuthenticationFilter(AuthServiceContract authService,
                                UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();
        logger.info("Request URI: {}", requestURI);

        /* ---------- Allow public endpoints ---------- */
        if (Arrays.asList(UNAUTHENTICATED_PATHS).contains(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        /* ---------- Handle CORS preflight ---------- */
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            setCORSHeaders(httpResponse);
            return;
        }

        /* ---------- Extract JWT token ---------- */
        String token = getAuthTokenFromCookies(httpRequest);

        if (token == null || !authService.validateToken(token)) {
            sendErrorResponse(httpResponse,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized: Invalid or missing token");
            return;
        }

        /* ---------- Extract user ---------- */
        String username = authService.extractUsername(token);
        Optional<User> userOptional =
                userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            sendErrorResponse(httpResponse,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Unauthorized: User not found");
            return;
        }

        User authenticatedUser = userOptional.get();
        Role role = authenticatedUser.getRole();

        logger.info("Authenticated user: {}, role: {}",
                authenticatedUser.getUsername(), role);

        /* ---------- ROLE-BASED ACCESS CONTROL ---------- */

        // Admin-only APIs
        if (requestURI.startsWith("/admin/")
                && role != Role.ADMIN) {

            sendErrorResponse(httpResponse,
                    HttpServletResponse.SC_FORBIDDEN,
                    "Forbidden: Admin access required");
            return;
        }

        // Customer-only APIs (EXCLUDE auth endpoints like logout)
        if (requestURI.startsWith("/api/")
                && !requestURI.startsWith("/api/auth/")
                && role != Role.CUSTOMER) {

            sendErrorResponse(httpResponse,
                    HttpServletResponse.SC_FORBIDDEN,
                    "Forbidden: Customer access required");
            return;
        }

        /* ---------- Attach authenticated user ---------- */
        httpRequest.setAttribute("authenticatedUser", authenticatedUser);

        chain.doFilter(request, response);
    }

    /* ================= Helper Methods ================= */

    private void setCORSHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        response.setHeader("Access-Control-Allow-Methods",
                "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers",
                "Content-Type, Authorization");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void sendErrorResponse(HttpServletResponse response,
                                   int statusCode,
                                   String message)
            throws IOException {
        response.setStatus(statusCode);
        response.getWriter().write(message);
    }

    private String getAuthTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            return Arrays.stream(cookies)
                    .filter(cookie -> "authToken".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
