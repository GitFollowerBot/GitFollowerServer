package gitfollower.server.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberAddResponse {
    private String nickname;

    public static MemberAddResponse withNickname(String nickname) {
        return new MemberAddResponse(nickname);
    }

    private MemberAddResponse(String nickname) {
        this.nickname = nickname;
    }
}
