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
    private static final String[] PUBLIC_ROUTES = {"/user/login", "/user/verify/code", "/user/register", "/purchaseRequisition/upload","/store/**","stock/all","stock/delete/**","stock/check/**", "/user/refresh/token", "/user/image", "/laptoplist/**", "/laptop/**", "/station/getAll", "/antivirus/**"};
    private static final String HTTP_OPTIONS_METHOD = "OPTIONS";
    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filter) throws ServletException, IOException {
        try {
            String token = getToken(request);
            // If no token is present, clear context and let Spring Security handle authentication failure
            if (token == null) {
                SecurityContextHolder.clearContext();
                filter.doFilter(request, response);
                return;
            }
            
            Long userId = getUserId(request);
            if (tokenProvider.isTokenValid(userId, token)) {
                List<GrantedAuthority> authorities = tokenProvider.getAuthorities(token);
                Authentication authentication = tokenProvider.getAuthentication(userId, authorities, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                SecurityContextHolder.clearContext();
            }
            filter.doFilter(request, response);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            processError(request, response, exception);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        // Skip filter for OPTIONS requests (CORS preflight)
        if (request.getMethod().equalsIgnoreCase(HTTP_OPTIONS_METHOD)) {
            return true;
        }
        // Skip filter for public routes (support pattern matching)
        for (String publicRoute : PUBLIC_ROUTES) {
            if (publicRoute.endsWith("/**")) {
                String prefix = publicRoute.substring(0, publicRoute.length() - 3);
                if (requestURI.startsWith(prefix)) {
                    return true;
                }
            } else if (requestURI.equals(publicRoute) || requestURI.startsWith(publicRoute + "/")) {
                return true;
            }
        }
        // For authenticated routes, always process the filter to handle authentication
        // Even if there's no token, let Spring Security handle the authentication failure
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



















