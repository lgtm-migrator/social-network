package com.schibsted.spain.friends.config;

import com.schibsted.spain.friends.utils.exceptions.InvalidCredentialException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Optional.ofNullable;

public class UserAuthenticationFilter extends AbstractAuthenticationProcessingFilter {


    protected UserAuthenticationFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    protected UserAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws InvalidCredentialException {
        final String token = ofNullable(request.getHeader("X-password")).orElseThrow(() -> new InvalidCredentialException("password missing"));
        final String user = ofNullable(request.getParameter("usernameFrom")).orElseThrow(() -> new InvalidCredentialException("username missing"));
        final Authentication authentication = new UsernamePasswordAuthenticationToken(user, token);
        return getAuthenticationManager().authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }
}
