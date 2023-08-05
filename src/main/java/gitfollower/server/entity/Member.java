package gitfollower.server.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;

    private String token;

    public static Member withNicknameAndToken(String nickname, String token) {
        return new Member(nickname, token);
    }

    private Member(String nickname, String token) {
        this.nickname = nickname;
        this.token = token;
    }
}