package gitfollower.server.exception;

public class NicknameNotFoundException extends RuntimeException {
    public static final String message = "유저를 찾을 수 없습니다.";

    public NicknameNotFoundException() {
    }

    public NicknameNotFoundException(String message) {
        super(message);
    }
}
