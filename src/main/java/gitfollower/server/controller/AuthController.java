package gitfollower.server.controller;

import gitfollower.server.dto.MemberAddResponse;
import gitfollower.server.dto.TokenDto;
import gitfollower.server.dto.LoginReq;
import gitfollower.server.dto.MemberAddRequest;
import gitfollower.server.dto.global.ApiSuccessResponse;
import gitfollower.server.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiSuccessResponse<MemberAddResponse>> register(
            HttpServletRequest request, @RequestBody MemberAddRequest req) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        request.getServletPath(),
                        authService.register(req)
                ));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiSuccessResponse<TokenDto>> login(
            HttpServletRequest request, @RequestBody LoginReq req) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        request.getServletPath(),
                        authService.login(req)
                ));
    }
}
