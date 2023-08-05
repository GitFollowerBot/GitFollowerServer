package gitfollower.server.controller;

import gitfollower.server.dto.ApiResponse;
import gitfollower.server.dto.MemberAddReq;
import gitfollower.server.exception.*;
import gitfollower.server.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody MemberAddReq req) {
        try {
            return authService.register(req);
        } catch (ConnectionException e) {
            ErrorText error = ErrorText.CONNECTION_ERROR;
            return new ApiResponse<>(error.getCode(), error.getBody());
        }
        catch (UnvalidGithubNicknameException e) {
            ErrorText error = ErrorText.UNVALID_GITHUB_USERNAME;
            return new ApiResponse<>(error.getCode(), error.getBody());
        }
        catch (NicknameDuplicatedException e) {
            ErrorText error = ErrorText.NICKNAME_DUPLICATE;
            return new ApiResponse<>(error.getCode(), error.getBody());
        } catch (UnAuthorizedGithubToken e) {
            ErrorText error = ErrorText.UNAUTHORIZED_GITHUB_TOKEN;
            return new ApiResponse<>(error.getCode(), error.getBody());
        }
    }
}
