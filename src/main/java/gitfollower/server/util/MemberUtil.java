package gitfollower.server.util;

import gitfollower.server.entity.Member;
import gitfollower.server.exception.NicknameNotFoundException;
import gitfollower.server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberUtil {

    private final MemberRepository memberRepository;

    public Member getLoggedInMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isUnAuthorizedAuthentication(authentication))
            return null;
        String nickname = authentication.getName();
        return memberRepository.findByNickname(nickname)
                .orElseThrow(() -> new NicknameNotFoundException(NicknameNotFoundException.message));
    }

    private static boolean isUnAuthorizedAuthentication(Authentication authentication) {
        return authentication == null;
    }
}