package gitfollower.server.exception.global;

import gitfollower.server.dto.global.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(annotations = RestController.class)
public class ApiExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiErrorResponse> general(HttpServletRequest request, GeneralException e) {
        ErrorCode errorCode = e.getErrorCode();
        HttpStatus status = errorCode.getStatus();

        return ResponseEntity
                .status(status)
                .body(ApiErrorResponse.of(
                        status,
                        request.getServletPath(),
                        e.getClass().getSimpleName(),
                        errorCode
                ));
    }
}