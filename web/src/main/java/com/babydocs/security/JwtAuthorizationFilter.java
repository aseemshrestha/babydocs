package com.babydocs.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.babydocs.logger.AppLogger;
import com.babydocs.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
public class JwtAuthorizationFilter extends BasicAuthenticationFilter
{
    private final UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
        UserRepository userRepository)
    {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        String header = request.getHeader(JwtProperties.ACCESS_TOKEN);
        if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        Authentication authentication = getUsernamePasswordAuthentication(request, response);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);

    }

    private Authentication getUsernamePasswordAuthentication(HttpServletRequest request, HttpServletResponse response)
    {
        try {
            String token = request.getHeader(JwtProperties.ACCESS_TOKEN);

            if (token != null) {
                String _token = token.substring(JwtProperties.TOKEN_PREFIX.length());
                Algorithm algorithm = Algorithm.HMAC512(JwtProperties.SECRET.getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(_token);
                String username = decodedJWT.getSubject();
                Claim role = decodedJWT.getClaim("role");
                List<GrantedAuthority> authorities = new ArrayList<>();
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.asString());
                authorities.add(authority);
                UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
                return auth;

            }
        } catch (com.auth0.jwt.exceptions.JWTDecodeException ex) {
            AppLogger.info(JwtAuthorizationFilter.class, "TOKEN CANNOT BE DECODED");
        } catch (java.lang.IllegalArgumentException ex) {
            AppLogger.info(JwtAuthorizationFilter.class, "BAD REQUEST");
        } catch (com.auth0.jwt.exceptions.TokenExpiredException ex) {
            AppLogger.info(JwtAuthorizationFilter.class, "TOKEN EXPIRED");
        }
        return null;
    }
}
