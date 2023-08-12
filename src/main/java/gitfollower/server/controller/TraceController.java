package gitfollower.server.controller;

import gitfollower.server.dto.global.ApiResponse;
import gitfollower.server.dto.global.ApiSuccessResponse;
import gitfollower.server.entity.Member;
import gitfollower.server.service.TraceService;
import gitfollower.server.util.MemberUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class TraceController {

    private final TraceService traceService;
    private final MemberUtil memberUtil;

    @GetMapping("/followers")
    public ResponseEntity<ApiSuccessResponse<String>> followers(HttpServletRequest request) {
        Member targetMember = memberUtil.getLoggedInMember();
        traceService.triggerTracingFollowers(targetMember);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        request.getServletPath(),
                        "완료"
                ));
    }

    @DeleteMapping("/stop")
    public ResponseEntity<ApiSuccessResponse<String>> stop(HttpServletRequest request) {
        Member targetMember = memberUtil.getLoggedInMember();
        traceService.stop(targetMember);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        request.getServletPath(),
                        "중지 완료"
                ));
    }

    @PostMapping("/restart")
    public ResponseEntity<ApiSuccessResponse<String>> restart(HttpServletRequest request) {
        Member targetMember = memberUtil.getLoggedInMember();
        traceService.restart(targetMember);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiSuccessResponse.of(
                        HttpStatus.OK,
                        request.getServletPath(),
                        "재시작 완료"
                ));
    }
}
