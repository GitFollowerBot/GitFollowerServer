package gitfollower.server.github;

import gitfollower.server.entity.Info;
import gitfollower.server.entity.Member;
import gitfollower.server.exception.ConnectionException;
import gitfollower.server.repository.InfoRepository;
import gitfollower.server.repository.MemberRepository;
import gitfollower.server.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GithubApi {

    GitHub gitHub;
    private final MemberRepository memberRepository;
    private final InfoRepository infoRepository;
    private final MemberUtil memberUtil;

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

            githubConnection(loggedInMember.getToken());

            GHUser githubUser = gitHub.getUser(loggedInMember.getNickname());

            addFollowAlert(loggedInMember, githubUser, result);
            unFollowAlert(loggedInMember, githubUser, result);

        } catch (IOException e) {
            throw new ConnectionException(ConnectionException.message);
        }
    }

    private boolean isNotLoggedIn(Member member) {
        return member == null;
    }

    @Transactional
    void addFollowAlert(Member loggedInMember, GHUser githubUser,
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

            Member existedMember = memberRepository.findByNickname(nickname).orElseGet(() -> {
                Member newMember = Member.withNicknameAndToken(nickname, null);
                memberRepository.save(newMember);
                return newMember;
            });

            Info newInfo = Info.withFollowerAndOwner(existedMember, loggedInMember);
            infoRepository.save(newInfo);
        }

        System.out.println("=======================================");
    }

    @Transactional
    void unFollowAlert(Member loggedInMember, GHUser githubUser,
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
}