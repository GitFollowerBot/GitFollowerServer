package gitfollower.server.exception;

public class NicknameDuplicatedException extends RuntimeException {
    public static final String message = "이미 등록되어 있는 유저입니다.";

    public NicknameDuplicatedException() {
    }

    public NicknameDuplicatedException(String message) {
        super(message);
    }
}
