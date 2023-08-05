package gitfollower.server.entity;

import gitfollower.server.dto.MemberAddReq;
import gitfollower.server.dto.MemberAddRes;
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

    public static Member from(MemberAddReq req) {
        return new Member(req.getNickname(), req.getToken());
    }

    private Member(String nickname, String token) {
        this.nickname = nickname;
        this.token = token;
    }
}