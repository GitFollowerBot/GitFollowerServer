package gitfollower.server.exception.global;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorCode {

    CONNECTION_ERROR(INTERNAL_SERVER_ERROR, "연결 오류입니다."),
    EMAIL_ERROR(INTERNAL_SERVER_ERROR, "이메일 전송 오류입니다."),
    NICKNAME_DUPLICATE(CONFLICT, "닉네임이 중복됩니다."),
    NICKNAME_NOT_FOUND(NOT_FOUND, "해당 닉네임을 가진 유저가 없습니다."),
    UNVALID_GITHUB_TOKEN(FORBIDDEN, "깃허브 토큰 주인이 일치하지 않습니다."),
    UNVALID_GITHUB_NICKNAME(NOT_FOUND, "해당 깃허브 닉네임을 가진 유저가 없습니다.");

    private final HttpStatus status;
    private final String message;

}
