package gitfollower.server.exception.global;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorDetail {

    private final String type;
    private final String message;

    public static ErrorDetail of(String type, ErrorCode error) {
        return new ErrorDetail(type, error.getMessage());
    }
}
