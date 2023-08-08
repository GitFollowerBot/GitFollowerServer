package gitfollower.server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorText {
    NICKNAME_DUPLICATE(400, "NicknameDuplicatedException", "이미 등록되어 있는 유저입니다."),
    CONNECTION_ERROR(400, "ConnectionException", "연결 오류입니다."),
    UNVALID_GITHUB_USERNAME(400, "UnvalidGithubNicknameException", "깃허브에서 찾을 수 없는 닉네임입니다."),
    UNAUTHORIZED_GITHUB_TOKEN(401, "UnAuthorizedGithubToken", "토큰 주인과 닉네임이 일치하지 않습니다."),
    NICKNAME_NOT_FOUND(404, "NicknameNotFoundException", "유저를 찾을 수 없습니다.");

    private final int code;
    private final String cause;
    private final String message;

    public String getBody() {
        return this.message;
    }
}
