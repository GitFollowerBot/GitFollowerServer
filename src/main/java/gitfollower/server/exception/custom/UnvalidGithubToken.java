package gitfollower.server.exception.custom;

import gitfollower.server.exception.global.ErrorCode;
import gitfollower.server.exception.global.GeneralException;

public class UnvalidGithubToken extends GeneralException {

    public static final ErrorCode errorCode = ErrorCode.UNVALID_GITHUB_TOKEN;

    public UnvalidGithubToken() {
        super(errorCode);
    }
}
