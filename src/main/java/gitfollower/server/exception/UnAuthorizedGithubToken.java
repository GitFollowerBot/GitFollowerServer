package gitfollower.server.exception;

public class UnAuthorizedGithubToken extends RuntimeException {
    public static final String message = "토큰 주인과 닉네임이 일치하지 않습니다.";

    public UnAuthorizedGithubToken() {
    }

    public UnAuthorizedGithubToken(String message) {
        super(message);
    }
}
