package gitfollower.server.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAddReq {
    private String nickname;
    private String token;

    public MemberAddReq withNicknameAndToken(String nickname, String token) {
        return new MemberAddReq(nickname, token);
    }

    private MemberAddReq(String nickname, String token) {
        this.nickname = nickname;
        this.token = token;
    }
}
