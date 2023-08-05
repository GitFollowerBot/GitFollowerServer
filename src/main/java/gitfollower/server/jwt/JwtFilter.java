package gitfollower.server.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
public class JwtFilter extends GenericFilterBean {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private final TokenProvider tokenProvider;

    public JwtFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String jwt = resolveToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();

        if (!isValidJwt(jwt)) {
            log.info(jwt);
            log.info("유효한 JWT 토큰 없음, uri = {}", requestURI);
            chain.doFilter(request, response);
            return;
        }

        Authentication authentication = tokenProvider.getAuthentication(jwt);
        setAuthenticationInSecurityContextHolder(authentication);

        log.info("Security Context에 '{}' 인증 정보 저장, uri = {}", authentication.getName(), requestURI);

        chain.doFilter(request, response);
    }

    private static void setAuthenticationInSecurityContextHolder(Authentication authentication) {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
    }

    private boolean isValidJwt(String jwt) {
        return StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (isValidBearerToken(bearerToken)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private static boolean isValidBearerToken(String bearerToken) {
        return StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ");
    }
}
