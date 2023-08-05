package gitfollower.server.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAddRes {
    private String nickname;

    public static MemberAddRes withNickname(String nickname) {
        return new MemberAddRes(nickname);
    }

    private MemberAddRes(String nickname) {
        this.nickname = nickname;
    }
}
