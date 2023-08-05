package gitfollower.server.github;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class GithubUrl {
    @Value("${github.prefix}")
    private String githubPrefix;

    @Value("${github.api-url}")
    private String githubApiUrl;
}
