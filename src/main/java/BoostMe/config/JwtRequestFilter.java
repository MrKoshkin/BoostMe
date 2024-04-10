package BoostMe.config;

import BoostMe.utils.JwtTokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        String username = null;
        String jwt = null;
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            jwt = authHeader.substring(BEARER_PREFIX.length());
            try {
                username = jwtTokenUtils.getUsername(jwt);
            } catch (ExpiredJwtException e) {
                log.debug("Token expired");
            } catch (SignatureException e) {
                log.debug("Invalid signature");
            }
        }
        if (username != null) {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticateUser(username, jwt);
            } else {
                log.debug("User already authenticated. Skipping authentication.");
            }
        }
        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String username, String jwt) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                username,
                null,
                jwtTokenUtils.getRoles(jwt).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
        );
        SecurityContextHolder.getContext().setAuthentication(token);
        log.debug("User authenticated: {}", username);
    }
}