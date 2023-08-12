package gitfollower.server.exception.custom;

import gitfollower.server.exception.global.ErrorCode;
import gitfollower.server.exception.global.GeneralException;

public class NicknameDuplicatedException extends GeneralException {

    public static final ErrorCode errorCode = ErrorCode.NICKNAME_DUPLICATE;

    public NicknameDuplicatedException() {
        super(errorCode);
    }
}
