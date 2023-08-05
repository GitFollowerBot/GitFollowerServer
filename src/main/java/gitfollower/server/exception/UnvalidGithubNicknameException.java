package gitfollower.server.exception;

public class UnvalidGithubNicknameException extends RuntimeException {
    public static final String message = "깃허브에서 찾을 수 없는 닉네임입니다.";
    public UnvalidGithubNicknameException() {
    }

    public UnvalidGithubNicknameException(String message) {
        super(message);
    }
}
