package gitfollower.server.service;

import gitfollower.server.dto.ApiResponse;
import gitfollower.server.dto.MemberAddReq;
import gitfollower.server.dto.MemberAddRes;
import gitfollower.server.entity.Member;
import gitfollower.server.exception.NicknameDuplicatedException;
import gitfollower.server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;

    public ApiResponse<MemberAddRes> register(MemberAddReq req) {
        // 회원 닉네임 검증
        if (!isUniqueNickname(req.getNickname())) {
            throw new NicknameDuplicatedException(NicknameDuplicatedException.message);
        }

        // 회원을 만들어줘야 함
        Member newMember = Member.from(req);
        memberRepository.save(newMember);

        // 응답 던져주기
        MemberAddRes result = MemberAddRes.withNickname(newMember.getNickname());

        return new ApiResponse<>(200, result);
    }

    private boolean isUniqueNickname(String nickname) {
        return memberRepository.findByNickname(nickname).isEmpty();
    }
}
