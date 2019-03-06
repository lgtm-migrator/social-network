package com.schibsted.spain.friends.config;

import org.springframework.security.web.RedirectStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * in case of authentication failure, the server should not redirect to any error page. The server will simply return an HTTP 401 (Unauthorized).
 */
public class NoRedirectStrategy implements RedirectStrategy {

    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        // no redirect strategy for rest
    }
}
