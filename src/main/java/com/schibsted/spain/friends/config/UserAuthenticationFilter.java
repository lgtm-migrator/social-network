package com.schibsted.spain.friends.config;

import com.schibsted.spain.friends.utils.exceptions.InvalidCredentialException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Optional.ofNullable;

@Slf4j
public class UserAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


    protected UserAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    protected UserAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        final String token = ofNullable(request.getHeader("X-password")).orElseThrow(() -> new InvalidCredentialException("password missing"));
        final String userParam = ofNullable(request.getParameter("usernameFrom")).orElse(request.getParameter("username"));
        final String user = ofNullable(userParam).orElseThrow(() -> new InvalidCredentialException("username missing"));
        final Authentication authentication = new UsernamePasswordAuthenticationToken(user, token);
        Authentication authenticate;
        try {
            authenticate = getAuthenticationManager().authenticate(authentication);
        } catch (AuthenticationException | InvalidCredentialException e) {
            log.error("Authentication error: {}", e.getMessage());
            throw new BadCredentialsException(e.getMessage(), e);
        }
        return authenticate;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }
}
