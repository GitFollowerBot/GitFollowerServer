package gitfollower.server.dto.global;

import gitfollower.server.exception.global.ErrorCode;
import gitfollower.server.exception.global.ErrorDetail;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiErrorResponse extends ApiResponse {

    private final ErrorDetail error;

    private ApiErrorResponse(HttpStatus status, String path, String type, ErrorCode error) {
        super(false, status, path);
        this.error = ErrorDetail.of(type, error);
    }

    public static ApiErrorResponse of(HttpStatus status, String path, String type, ErrorCode error) {
        return new ApiErrorResponse(status, path, type, error);
    }
}
