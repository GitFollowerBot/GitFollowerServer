package gitfollower.server.github;

import gitfollower.server.entity.Member;
import gitfollower.server.exception.ConnectionException;
import gitfollower.server.repository.MemberRepository;
import gitfollower.server.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHPerson;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GithubApi {

    GitHub gitHub;
    private final MemberRepository memberRepository;
    private final MemberUtil memberUtil;

    private void githubConnection(String token) throws IOException {
        gitHub = new GitHubBuilder().withOAuthToken(token).build();
        gitHub.checkApiUrlValidity();
    }

    public void getFollowers() {
        try {
            Member loggedInMember = memberUtil.getLoggedInMember();

            githubConnection(loggedInMember.getToken());

            GHUser githubUser = gitHub.getUser(loggedInMember.getNickname());

            /* 임시 테스트 */
            List<String> followers = githubUser.getFollowers().stream().map(GHPerson::getLogin).toList();

            for (String follower : followers) {
                System.out.println("follower = " + follower);
            }
        } catch (IOException e) {
            throw new ConnectionException(ConnectionException.message);
        }
    }
}
