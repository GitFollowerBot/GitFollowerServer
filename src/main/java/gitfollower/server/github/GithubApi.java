package gitfollower.server.github;

import gitfollower.server.entity.Info;
import gitfollower.server.entity.Member;
import gitfollower.server.exception.custom.ConnectionException;
import gitfollower.server.exception.custom.MailingException;
import gitfollower.server.repository.InfoRepository;
import gitfollower.server.repository.MemberRepository;
import gitfollower.server.util.MemberUtil;
import gitfollower.server.util.TokenUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GithubApi {

    GitHub gitHub;
    private final MemberRepository memberRepository;
    private final InfoRepository infoRepository;
    private final MemberUtil memberUtil;
    private final TokenUtil tokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender = new JavaMailSenderImpl(); // 오류 있음

    @Value("${discord.webhook}")
    private String discord;

    @Value("${spring.mail.background}")
    private String emailBackground;

    private void githubConnection(String token) throws IOException {
        gitHub = new GitHubBuilder().withOAuthToken(token).build();
        gitHub.checkApiUrlValidity();
    }

    public void getFollowers() {
        try {
            HashMap<String, ArrayList<String>> result = new HashMap<>();
            result.put("follow", new ArrayList<>());
            result.put("unfollow", new ArrayList<>());

            Member loggedInMember = memberUtil.getLoggedInMember();

            // 암호화 된 토큰 일치 검사
            if (!passwordEncoder.matches(tokenUtil.getRawToken(), loggedInMember.getToken()))
                return;

            githubConnection(tokenUtil.getRawToken());

            GHUser githubUser = gitHub.getUser(loggedInMember.getNickname());

            addFollowAlert(loggedInMember, githubUser, result);
            unFollowAlert(loggedInMember, githubUser, result);

            discordAlert(result);
            // emailAlert(githubUser, result);
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

    private boolean isNotLoggedIn(Member member) {
        return member == null;
    }

    @Transactional
    public void addFollowAlert(Member loggedInMember, GHUser githubUser,
                               HashMap<String, ArrayList<String>> map) throws IOException {
        List<String> afterFollowers = getAfterFollowersName(githubUser);

        System.out.println("========== [새로 추가된 유저 목록] ==========");

        for (String nickname : afterFollowers) {
            List<Info> savedFollowerInfo = infoRepository.findAllByOwner(loggedInMember).stream().filter(
                    x -> x.getFollower().getNickname().equals(nickname)
            ).toList();

            if (isAlreadyFollow(savedFollowerInfo))
                continue;

            System.out.println(nickname);

            ArrayList<String> followResultList = map.get("follow");
            followResultList.add(nickname);
            map.put("follow", followResultList);

            Member existedMember = memberRepository.findByNickname(nickname).orElseGet(() -> createMember(nickname));

            Info newInfo = Info.withFollowerAndOwner(existedMember, loggedInMember);
            infoRepository.save(newInfo);
        }

        System.out.println("=======================================");
    }

    @Transactional
    public Member createMember(String nickname) {
        Member newMember = Member.withNicknameAndToken(nickname, null);
        return memberRepository.save(newMember);
    }

    @Transactional
    public void unFollowAlert(Member loggedInMember, GHUser githubUser,
                              HashMap<String, ArrayList<String>> map) throws IOException {

        List<String> afterFollowers = getAfterFollowersName(githubUser);
        List<String> prevFollowers = getPrevFollowersName(loggedInMember);

        System.out.println("========== [언팔로우된 유저 목록] ===========");

        for (String nickname : prevFollowers) {
            if (afterFollowers.contains(nickname))
                continue;

            GHUser destroyUser = gitHub.getUser(nickname);
            memberRepository.findByNickname(destroyUser.getLogin()).ifPresent(
                    infoRepository::deleteByFollower
            );

            System.out.println(nickname);

            ArrayList<String> unfollowResultList = map.get("unfollow");
            unfollowResultList.add(nickname);
            map.put("unfollow", unfollowResultList);
        }

        System.out.println("=======================================");

    }

    private List<String> getPrevFollowersName(Member loggedInMember) {
        return infoRepository
                .findAllByOwner(loggedInMember)
                .stream()
                .map(Info::getFollower)
                .toList().stream().map(Member::getNickname).toList();
    }

    private static List<String> getAfterFollowersName(GHUser githubUser) throws IOException {
        return githubUser.getFollowers().stream().map(GHPerson::getLogin).toList();
    }

    private static boolean isAlreadyFollow(List<Info> savedFollowerInfo) {
        return !savedFollowerInfo.isEmpty();
    }

    public void discordAlert(HashMap<String, ArrayList<String>> map) {
        // follow와 unfollow 리스트 모두 비어 있을 때, 디스코드에 메시지를 보내지 않습니다.
        if (isNotNewInformation(map)) {
            return;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 메시지 내용을 구성할 StringBuilder 생성
        StringBuilder contentBuilder = new StringBuilder();

        // 신규 팔로워가 있을 경우, 해당 리스트를 문자열로 구성합니다.
        ArrayList<String> newFollowers = map.get("follow");
        if (!newFollowers.isEmpty()) {
            contentBuilder.append("다음 분들이 팔로우 해주셨습니다! 🎉\n");
            for (String follower : newFollowers) {
                contentBuilder.append(follower).append("\n");
            }
        }

        // 언팔로워가 있을 경우, 해당 리스트를 문자열로 구성합니다.
        ArrayList<String> unfollowers = map.get("unfollow");
        if (!unfollowers.isEmpty()) {
            contentBuilder.append("다음 팔로워들을 잃었습니다 😭\n");
            for (String unfollower : unfollowers) {
                contentBuilder.append(unfollower).append("\n");
            }
        }

        // 최종적으로 보낼 JSON 데이터를 구성합니다.
        String jsonBody = "{\"content\": \"" + escapeJsonString(contentBuilder.toString()) + "\"}";

        RestTemplate template = new RestTemplate();
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        template.postForObject(discord, entity, String.class);
    }

    // 문자열 내의 이스케이프해야 할 문자를 처리합니다.
    private String escapeJsonString(String jsonString) {
        return jsonString
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private void emailAlert(GHUser githubUser, HashMap<String, ArrayList<String>> result) {
        // follow와 unfollow 리스트 모두 비어 있을 때, 디스코드에 메시지를 보내지 않습니다.
        if (isNotNewInformation(result)) {
            return;
        }

        try {
            String receiveEmail = githubUser.getEmail();
            MimeMessage newMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(newMessage, true);

            setEmailTarget(receiveEmail, messageHelper);
            appendEmilTitle(messageHelper);

            StringBuilder emailContentBuilder = new StringBuilder();

            appendEmailBackgroundImage(emailContentBuilder);

            // 추가: 팔로우와 언팔로우 정보를 이메일 내용에 추가
            ArrayList<String> newFollowers = result.get("follow");
            ArrayList<String> unfollowers = result.get("unfollow");

            if (!newFollowers.isEmpty()) {
                appendNewFollowersInfo(emailContentBuilder, newFollowers);
            }

            if (!unfollowers.isEmpty()) {
                appendUnFollowersInfo(emailContentBuilder, unfollowers);
            }

            messageHelper.setText(emailContentBuilder.toString(), true);
            javaMailSender.send(newMessage);
        } catch (MessagingException e) {
            throw new MailingException();
        } catch (IOException e) {
            throw new ConnectionException();
        }
    }

    private static boolean isNotNewInformation(HashMap<String, ArrayList<String>> result) {
        return result.get("follow").isEmpty() && result.get("unfollow").isEmpty();
    }

    private void appendUnFollowersInfo(StringBuilder emailContentBuilder, ArrayList<String> unfollowers) {
        emailContentBuilder.append("<h3>😭 다음 팔로워들을 잃었습니다 😭</h3>\n");
        emailContentBuilder.append(convertUnfollowersToString(unfollowers));
    }

    private void appendNewFollowersInfo(StringBuilder emailContentBuilder, ArrayList<String> newFollowers) {
        emailContentBuilder.append("<h3>🎉 다음 분들이 팔로우 해주셨습니다! 🎉</h3>\n");
        emailContentBuilder.append(convertFollowersToString(newFollowers));
    }

    private static void setEmailTarget(String receiveEmail, MimeMessageHelper messageHelper) throws MessagingException {
        messageHelper.setTo(receiveEmail);
    }

    private static void appendEmilTitle(MimeMessageHelper messageHelper) throws MessagingException {
        messageHelper.setSubject("📢 [GitFollower] 새로운 변경 내역입니다. 📢");
    }

    private void appendEmailBackgroundImage(StringBuilder emailContentBuilder) {
        emailContentBuilder.append("<img src=\"" +
                emailBackground +
                "\" width=\"700\" height=\"200\" style=\"display: block;\"></img>\n\n"); // 이미지 URL 및 스타일 적용
    }


    private String convertFollowersToString(List<String> followers) {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul>");
        for (String follower : followers) {
            sb.append("<li>").append(follower).append("</li>");
        }
        sb.append("</ul>");
        return sb.toString().trim(); // trim() 메서드로 빈 줄 제거
    }

    private String convertUnfollowersToString(List<String> unfollowers) {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul>");
        for (String unfollower : unfollowers) {
            sb.append("<li>").append(unfollower).append("</li>");
        }
        sb.append("</ul>");
        return sb.toString().trim(); // trim() 메서드로 빈 줄 제거
    }


}
