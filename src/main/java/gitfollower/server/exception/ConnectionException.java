package gitfollower.server.exception;

public class ConnectionException extends RuntimeException {
    public static final String message = "연결 오류입니다.";

    public ConnectionException() {
    }

    public ConnectionException(String message) {
        super(message);
    }
}
