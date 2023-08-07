package gitfollower.server.controller;

import gitfollower.server.dto.ApiResponse;
import gitfollower.server.dto.LoginReq;
import gitfollower.server.dto.MemberAddReq;
import gitfollower.server.dto.TokenDto;
import gitfollower.server.exception.*;
import gitfollower.server.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/register")
    public String getRegister() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String nickname, @RequestParam String token, Model model) {
        try {
            MemberAddReq req = MemberAddReq.withNicknameAndToken(nickname, token);
            authService.register(req);
            return "redirect:/trace";
        } catch (ConnectionException e) {
            ErrorText error = ErrorText.CONNECTION_ERROR;
            model.addAttribute("errorMessage", error.getBody());
        }
        catch (UnvalidGithubNicknameException e) {
            ErrorText error = ErrorText.UNVALID_GITHUB_USERNAME;
            model.addAttribute("errorMessage", error.getBody());
        }
        catch (NicknameDuplicatedException e) {
            ErrorText error = ErrorText.NICKNAME_DUPLICATE;
            model.addAttribute("errorMessage", error.getBody());
        } catch (UnAuthorizedGithubToken e) {
            ErrorText error = ErrorText.UNAUTHORIZED_GITHUB_TOKEN;
            model.addAttribute("errorMessage", error.getBody());
        }
        return "register";
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginReq req) {
        try {
            return new ApiResponse<>(200, authService.login(req));
        } catch (NicknameNotFoundException e) {
            ErrorText error = ErrorText.NICKNAME_NOT_FOUND;
            return new ApiResponse<>(error.getCode(), error.getBody());
        }
    }
}
