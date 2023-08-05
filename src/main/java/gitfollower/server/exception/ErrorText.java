package gitfollower.server.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ErrorText {
    NICKNAME_DUPLICATE(400, "NicknameDuplicatedException", "이미 등록되어 있는 유저입니다.");

    private final int code;
    private final String cause;
    private final String message;

    public Map<String, String> getBody() {
        HashMap<String, String> body = new HashMap<>();
        body.put(getCause(), getMessage());

        return body;
    }
}
