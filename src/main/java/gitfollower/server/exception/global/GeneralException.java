package gitfollower.server.exception.global;

import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

    private final ErrorCode errorCode;

    protected GeneralException(ErrorCode errorCode) {
        super();
        this.errorCode = errorCode;
    }
}
