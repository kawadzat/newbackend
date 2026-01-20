package io.getarrays.securecapita.filter;

import io.getarrays.securecapita.provider.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static io.getarrays.securecapita.utils.ExceptionUtils.processError;
   import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * @author Junior RT
 * @version 1.0
 * @license Get Arrays, LLC (https://getarrays.io)
 * @since 1/2/2023
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String[] PUBLIC_ROUTES = {"/user/login", "/user/verify/code", "/user/register","/user/**", "/purchaseRequisition/upload","/store/**","stock/all","stock/delete/**","stock/check/**", "/user/refresh/token", "/user/image", "/laptoplist/**", "/laptop/**", "/station/getAll", "/antivirus/all"};
    private static final String HTTP_OPTIONS_METHOD = "OPTIONS";
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filter) throws ServletException, IOException {
        try {
            String requestPath = request.getRequestURI();
            String token = getToken(request);
            
            // Log token status for /station/getAll requests
            if (requestPath != null && requestPath.contains("/station/getAll")) {
                System.out.println("==========================================");
                System.out.println("CustomAuthorizationFilter: Processing /station/getAll");
                System.out.println("Token present: " + (token != null ? "YES" : "NO"));
                if (token == null) {
                    System.out.println("WARNING: No Authorization header found in request!");
                    System.out.println("Frontend AuthInterceptor is skipping token for public endpoint.");
                }
                System.out.println("==========================================");
            }
            
            // If no token is present, continue without authentication (endpoints are public)
            if (token == null) {
                SecurityContextHolder.clearContext();
                filter.doFilter(request, response);
                return;
            }
            
            // ALWAYS try to extract userId from token FIRST, even if validation might fail
            Long userId = null;
            try {
                // First try normal extraction with verification
                userId = getUserId(request);
                // Store userId in request attribute so it's always available, even if auth fails
                if (userId != null) {
                    request.setAttribute("userId", userId);
                    System.out.println("Extracted userId from token (verified): " + userId + " (stored in request)");
                }
            } catch (Exception extractException) {
                // If verification fails (e.g., expired token), try decoding without verification
                try {
                    userId = tokenProvider.getSubjectWithoutVerification(token);
                    if (userId != null) {
                        request.setAttribute("userId", userId);
                        System.out.println("Extracted userId from token (unverified/expired): " + userId + " (stored in request)");
                        if (requestPath != null && requestPath.contains("/station/getAll")) {
                            System.out.println("Note: Token may be expired, but userId was extracted successfully");
                        }
                    } else {
                        if (requestPath != null && requestPath.contains("/station/getAll")) {
                            System.out.println("ERROR: Could not extract userId from token even without verification");
                            System.out.println("Token may be malformed or missing subject claim");
                        }
                    }
                } catch (Exception decodeException) {
                    // Even decoding failed - token is completely invalid
                    if (requestPath != null && requestPath.contains("/station/getAll")) {
                        System.out.println("ERROR: Could not extract userId from token: " + extractException.getMessage());
                        System.out.println("Decoding also failed: " + decodeException.getMessage());
                    }
                    log.debug("Could not extract userId from token: {}", extractException.getMessage());
                }
            }
            
            // Now try to validate and set authentication if token is valid
            try {
                if (userId != null && tokenProvider.isTokenValid(userId, token)) {
                    List<GrantedAuthority> authorities = tokenProvider.getAuthorities(token);
                    Authentication authentication = tokenProvider.getAuthentication(userId, authorities, request);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    if (requestPath != null && requestPath.contains("/station/getAll")) {
                        System.out.println("SUCCESS: Authentication set for userId: " + userId);
                    }
                    log.debug("Authentication set for userId: {}", userId);
                } else {
                    // Token exists but is invalid - userId is still available in request attribute
                    if (requestPath != null && requestPath.contains("/station/getAll")) {
                        System.out.println("WARNING: Token validation failed, but userId " + userId + " is available in request");
                    }
                    SecurityContextHolder.clearContext();
                    log.debug("Token is invalid, but userId {} is stored in request", userId);
                }
            } catch (Exception tokenException) {
                // Token validation failed, but userId might still be available from earlier extraction
                if (requestPath != null && requestPath.contains("/station/getAll")) {
                    System.out.println("WARNING: Token validation error: " + tokenException.getMessage());
                    System.out.println("UserId " + userId + " is still available in request attribute");
                }
                SecurityContextHolder.clearContext();
                log.debug("Token validation error: {}, but userId {} may be available", tokenException.getMessage(), userId);
            }
            
            filter.doFilter(request, response);
        } catch (Exception exception) {
            log.error("Error in CustomAuthorizationFilter: {}", exception.getMessage());
            // Don't block the request even if there's an error (endpoints are public)
            SecurityContextHolder.clearContext();
            filter.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Skip filter for OPTIONS requests (CORS preflight)
        if (request.getMethod().equalsIgnoreCase(HTTP_OPTIONS_METHOD)) {
            return true;
        }
        // Process filter for all other requests to extract authentication if token is present
        // Even though endpoints are public, we still want to set authentication if a valid token is provided
        return false;
    }

    private Long getUserId(HttpServletRequest request) {
        return tokenProvider.getSubject(getToken(request), request);
    }

    private String getToken(HttpServletRequest request) {
        return ofNullable(request.getHeader(AUTHORIZATION))
                .filter(header -> header.startsWith(TOKEN_PREFIX))
                .map(token -> token.replace(TOKEN_PREFIX, EMPTY)).orElse(null);
    }
}



















