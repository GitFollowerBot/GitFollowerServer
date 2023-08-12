package gitfollower.server.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAddRequest {
    private String nickname;
    private String token;

    public static MemberAddRequest withNicknameAndToken(String nickname, String token) {
        return new MemberAddRequest(nickname, token);
    }

    private MemberAddRequest(String nickname, String token) {
        this.nickname = nickname;
        this.token = token;
    }

    public void updateTokenSecurity(String token) {
        this.token = token;
    }
}
