package gitfollower.server.controller;

import gitfollower.server.dto.ApiResponse;
import gitfollower.server.entity.Member;
import gitfollower.server.exception.ConnectionException;
import gitfollower.server.exception.ErrorText;
import gitfollower.server.service.TraceService;
import gitfollower.server.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class TraceController {

    private final TraceService traceService;
    private final MemberUtil memberUtil;

    @GetMapping("/followers")
    public ApiResponse<?> followers() {
        try {
            Member targetMember = memberUtil.getLoggedInMember();
            traceService.triggerTracingFollowers(targetMember);
            return null;
        } catch (ConnectionException e) {
            ErrorText error = ErrorText.CONNECTION_ERROR;
            return new ApiResponse<>(error.getCode(), error.getBody());
        }
    }

    @DeleteMapping("/stop")
    public ApiResponse<String> stop() {
        Member targetMember = memberUtil.getLoggedInMember();
        traceService.stop(targetMember);
        return new ApiResponse<>(200, "중지 완료");
    }

    @PostMapping("/restart")
    public ApiResponse<String> restart() {
        Member targetMember = memberUtil.getLoggedInMember();
        traceService.restart(targetMember);
        return new ApiResponse<>(200, "재시작 완료");
    }
}
