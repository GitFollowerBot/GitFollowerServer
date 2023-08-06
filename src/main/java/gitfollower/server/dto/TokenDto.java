package gitfollower.server.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenDto {
    private String token;

    public static TokenDto withToken(String token) {
        return new TokenDto(token);
    }

    private TokenDto(String token) {
        this.token = token;
    }
}
