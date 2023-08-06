package gitfollower.server.service;

import gitfollower.server.entity.Member;
import gitfollower.server.github.GithubApi;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class TraceService {
    private final GithubApi githubApi;
    private Member member;

    public void triggerTracingFollowers(Member member) {
        this.member = member;
        tracingFollowers();
    }

    @Scheduled(fixedRate = 5000, initialDelay = 3000)
    public void tracingFollowers() {
        if (!isExistMember(member))
            return;

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getNickname(), null,
                Collections.singleton(simpleGrantedAuthority));
        setAuthenticationInSecurityContextHolder(authentication);

        githubApi.getFollowers();
    }

    private static boolean isExistMember(Member member) {
        return member != null;
    }

    // JwtFilter에서 썼던 표현 재활용
    private static void setAuthenticationInSecurityContextHolder(Authentication authentication) {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
    }
}
