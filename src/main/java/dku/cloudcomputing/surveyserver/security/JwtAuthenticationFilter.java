package dku.cloudcomputing.surveyserver.security;

import dku.cloudcomputing.surveyserver.exception.member.NoSuchMemberException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtAuthenticator jwtProvider;
    public JwtAuthenticationFilter(JwtAuthenticator jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtProvider.resolveToken((HttpServletRequest) request);

        if(token == null || !jwtProvider.validateToken(token)) return;
        token = token.split(" ")[1].trim();
        try {
            Authentication auth = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (NoSuchMemberException e) {

        }

        chain.doFilter(request, response);
    }
}
