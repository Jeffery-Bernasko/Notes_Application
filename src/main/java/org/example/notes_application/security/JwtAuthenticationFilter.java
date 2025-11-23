package org.example.notes_application.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final JwtUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException
    {
        String header =request.getHeader("Authorization");
        String token = null;
        if(header != null && header.startsWith("Bearer ")){
             token = header.substring(7);
            try {
                if(jwtProvider.validateToken(token)){
                    String email =jwtProvider.getEmailFromToken(token);

                    // Only set authentication if it's not already set
                    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } else {
                        logger.warn("Token is valid but user is already authenticated or email is null");
                    }
                }
            } catch (Exception e){
                logger.error("Cannot set user authentication: {}", e);
            }
        } else {
            if(request.getCookies() != null){
                for(Cookie cookie : request.getCookies()){
                    if("jwt".equals(cookie.getName())){
                        cookie.getValue();
                        break;
                    }
                }
            }
        }
            filterChain.doFilter(request,response);
    }
}
