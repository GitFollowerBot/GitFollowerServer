package gitfollower.server.controller;

import gitfollower.server.dto.ApiResponse;
import gitfollower.server.exception.ConnectionException;
import gitfollower.server.exception.ErrorText;
import gitfollower.server.github.GithubApi;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class TraceController {

    private final GithubApi githubApi;

    @GetMapping("/followers")
    public ApiResponse<?> followers() {
        try {
            githubApi.getFollowers();
            return null;
        } catch (ConnectionException e) {
            ErrorText error = ErrorText.CONNECTION_ERROR;
            return new ApiResponse<>(error.getCode(), error.getBody());
        }
    }
}
