package com.ticketgo.filter;

import com.ticketgo.config.security.SecurityWhiteList;
import com.ticketgo.constant.RedisKeys;
import com.ticketgo.service.UserService;
import com.ticketgo.util.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtil;
    private final UserService userService;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final RedissonClient redisson;

    public JwtFilter(JwtUtils jwtUtil,
                     @Lazy UserService userService,
                     HandlerExceptionResolver handlerExceptionResolver,
                     RedissonClient redisson) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.redisson = redisson;
    }

    private static final Set<String> EXCLUDE_URL_PATTERNS = Arrays.stream(SecurityWhiteList.WHITELIST_PATHS)
                                                                    .collect(Collectors.toSet());

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        AntPathMatcher pathMatcher = new AntPathMatcher();

        boolean shouldNotFilter=  EXCLUDE_URL_PATTERNS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));

        if (shouldNotFilter) {
            log.info("Path excluded from filtering: {}", path);
        }

        return shouldNotFilter;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) {

        final String accessToken = resolveToken(request);
        if (accessToken == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        try {
            final String username = jwtUtil.extractUsername(accessToken);
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            if (username != null && authentication == null) {
                UserDetails user = userService.loadUserByUsername(username);

                if(checkUser(user.getUsername())) {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    return;
                }

                if (jwtUtil.isTokenValid(accessToken)) {
                    UsernamePasswordAuthenticationToken token =
                            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(token);
                }
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }

    private boolean checkUser(String username) {
        String redisKey = RedisKeys.blackListUserKey;
        RList<String> blackList = redisson.getList(redisKey);
        return blackList.contains(username);
    }

    private String resolveToken(@NonNull HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
