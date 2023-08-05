package gitfollower.server.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse <T> {
    private int code;
    private T data;

    public ApiResponse(int code, T data) {
        this.code = code;
        this.data = data;
    }
}
