package gitfollower.server.entity;

import gitfollower.server.dto.MemberAddRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nickname;

    private String token;

    private boolean trace;

    public static Member withNicknameAndToken(String nickname, String token) {
        return new Member(nickname, token);
    }

    public static Member from(MemberAddRequest req) {
        return new Member(req.getNickname(), req.getToken());
    }

    private Member(String nickname, String token) {
        this.nickname = nickname;
        this.token = token;
        this.trace = true;
    }

    public void updateTraceToFalse() {
        this.trace = false;
    }

    public void updateTraceToTrue() {
        this.trace = true;
    }

    public void updateToken(String token) {
        this.token = token;
    }

}