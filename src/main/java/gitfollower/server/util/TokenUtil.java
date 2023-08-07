package gitfollower.server.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenUtil {
    private String rawToken;

    public void updateToken(String token) {
        this.rawToken = token;
    }
}
