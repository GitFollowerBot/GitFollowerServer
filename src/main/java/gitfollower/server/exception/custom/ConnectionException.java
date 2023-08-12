package gitfollower.server.exception.custom;

import gitfollower.server.exception.global.ErrorCode;
import gitfollower.server.exception.global.GeneralException;

public class ConnectionException extends GeneralException {

    public static final ErrorCode errorCode = ErrorCode.CONNECTION_ERROR;

    public ConnectionException() {
        super(errorCode);
    }
}
