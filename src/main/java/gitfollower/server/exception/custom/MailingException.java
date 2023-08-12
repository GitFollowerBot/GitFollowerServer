package gitfollower.server.exception.custom;

import gitfollower.server.exception.global.ErrorCode;
import gitfollower.server.exception.global.GeneralException;

public class MailingException extends GeneralException {

    public static final ErrorCode errorCode = ErrorCode.EMAIL_ERROR;

    public MailingException() {
        super(errorCode);
    }
}
