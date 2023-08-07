package gitfollower.server.exception;

public class MailingException extends RuntimeException {
    public static final String message = "이메일 관련 에러가 발생했습니다.";

    public MailingException() {
    }

    public MailingException(String message) {
        super(message);
    }
}
