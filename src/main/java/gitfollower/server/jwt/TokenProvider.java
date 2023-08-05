package gitfollower.server.jwt;

import gitfollower.server.entity.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private final long tokenValidityInMilliseconds;
    private Key key;

    public TokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Decoders.BASE64URL.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(Authentication authentication, Member member) {
        String authorities = generateAuthoritiesValue(authentication);
        long now = checkNowTime();
        Date validity = generateValidity(now);

        return generateJwt(authentication, member, authorities, validity);
    }

    private static String generateAuthoritiesValue(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private static long checkNowTime() {
        return (new Date()).getTime();
    }

    private Date generateValidity(long now) {
        return new Date(now + this.tokenValidityInMilliseconds);
    }

    private String generateJwt(Authentication authentication, Member member, String authorities, Date validity) {
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .claim("member_id", member.getId())
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaimsInJwt(token);

        Collection<? extends GrantedAuthority> authorities =
                getAuthoritiesInClaims(claims);

        User principal = getPrincipal(claims, authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private static List<SimpleGrantedAuthority> getAuthoritiesInClaims(Claims claims) {
        return Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new).toList();
    }

    private static User getPrincipal(Claims claims, Collection<? extends GrantedAuthority> authorities) {
        return new User(claims.getSubject(), "", authorities);
    }

    private Claims getClaimsInJwt(String token) {
        JwtParser parsingJwt = getParsingJwt();

        return parsingJwt.parseClaimsJws(token).getBody();
    }

    private JwtParser getParsingJwt() {
        return Jwts.parserBuilder().setSigningKey(key).build();
    }

    public boolean validateToken(String token) {
        try {
            JwtParser parsingJwt = getParsingJwt();
            parsingJwt.parseClaimsJws(token);

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 서명");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못됨");
        }
        return false;
    }
}
