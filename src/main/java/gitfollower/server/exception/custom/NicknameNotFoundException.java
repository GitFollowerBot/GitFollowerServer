package gitfollower.server.exception.custom;

import gitfollower.server.exception.global.ErrorCode;
import gitfollower.server.exception.global.GeneralException;

public class NicknameNotFoundException extends GeneralException {

    public static final ErrorCode errorCode = ErrorCode.NICKNAME_NOT_FOUND;

    public NicknameNotFoundException() {
        super(errorCode);
    }
}
