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
        
        try {
            if(token == null || !jwtProvider.validateToken(token)) throw new RuntimeException();
            token = token.split(" ")[1].trim();
            Authentication auth = jwtProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (RuntimeException e) {

        }

        chain.doFilter(request, response);
    }
}
