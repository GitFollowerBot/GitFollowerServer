package gitfollower.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import gitfollower.server.dto.ApiResponse;
import gitfollower.server.dto.MemberAddReq;
import gitfollower.server.dto.MemberAddRes;
import gitfollower.server.entity.Member;
import gitfollower.server.exception.ConnectionException;
import gitfollower.server.exception.NicknameDuplicatedException;
import gitfollower.server.exception.UnAuthorizedGithubToken;
import gitfollower.server.exception.UnvalidGithubNicknameException;
import gitfollower.server.github.GithubUrl;
import gitfollower.server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final GithubUrl githubApiUrl;
    private final MemberRepository memberRepository;

    public ApiResponse<MemberAddRes> register(MemberAddReq req) {
        // 회원 닉네임 검증
        checkValidGithubUsername(req.getNickname());
        checkUniqueNickname(req.getNickname());
        checkTokenOwnerEqualsNickname(req.getToken(), req.getNickname());

        // 회원을 만들어줘야 함
        Member newMember = Member.from(req);
        memberRepository.save(newMember);

        // 응답 던져주기
        MemberAddRes result = MemberAddRes.withNickname(newMember.getNickname());

        return new ApiResponse<>(200, result);
    }


    private void checkUniqueNickname(String nickname) {
        memberRepository.findByNickname(nickname)
                .ifPresent(exist -> {
                    throw new NicknameDuplicatedException(NicknameDuplicatedException.message);
                });
    }

    private void checkValidGithubUsername(String username) {
        try {
            String urlValue = generateGithubUrl(username);
            URL githubUrl = new URL(urlValue);
            HttpURLConnection gitConnection = (HttpURLConnection) githubUrl.openConnection();
            gitConnection.setRequestMethod("GET");

            int responseCode = gitConnection.getResponseCode();
            if (!isResponseCodeSuccessful(responseCode))
                throw new UnvalidGithubNicknameException(UnvalidGithubNicknameException.message);

            gitConnection.disconnect();
        } catch (IOException e) {
            throw new ConnectionException(ConnectionException.message);
        }
    }

    private static boolean isResponseCodeSuccessful(int responseCode) {
        return responseCode == 200;
    }

    private void checkTokenOwnerEqualsNickname(String token, String nickname) {
        ResponseEntity<JsonNode> response = getResponseInGithubUsingToken(token);
        JsonNode responseBody = response.getBody();
        if (responseBody == null || !responseBody.has("login") || !responseBody.get("login").asText().equals(nickname)) {
            throw new UnAuthorizedGithubToken(UnAuthorizedGithubToken.message);
        }
    }

    private ResponseEntity<JsonNode> getResponseInGithubUsingToken(String token) {
        HttpHeaders headers = createHttpHeaders(token);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> response =
                restTemplate.exchange(githubApiUrl.getGithubApiUrl(), HttpMethod.GET, requestEntity, JsonNode.class);
        // 이미 깃허브 상에서 닉네임이 존재하는지 여부는 마쳤기 때문에
        // 이 과정에서 response.getStatusCode가 200이 아니라면 연결 문제라고 설정하였습니다.
        if (!isRestTemplateConnectionSuccessful(response)) {
            throw new ConnectionException(ConnectionException.message);
        }
        return response;
    }

    private static HttpHeaders createHttpHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }

    private static boolean isRestTemplateConnectionSuccessful(ResponseEntity<JsonNode> response) {
        return response.getStatusCode() == HttpStatus.OK;
    }

    private String generateGithubUrl(String username) {
        return githubApiUrl.getGithubPrefix() + username;
    }
}