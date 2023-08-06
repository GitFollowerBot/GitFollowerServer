package gitfollower.server.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginReq {
    private String nickname;
    private String token;

    public LoginReq withNicknameAndToken(String nickname, String token) {
        return new LoginReq(nickname, token);
    }

    public LoginReq(String nickname, String token) {
        this.nickname = nickname;
        this.token = token;
    }
}
