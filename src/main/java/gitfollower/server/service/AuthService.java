package gitfollower.server.service;

import com.fasterxml.jackson.databind.JsonNode;
import gitfollower.server.dto.*;
import gitfollower.server.entity.Member;
import gitfollower.server.exception.*;
import gitfollower.server.jwt.JwtFilter;
import gitfollower.server.jwt.TokenProvider;
import gitfollower.server.repository.MemberRepository;
import gitfollower.server.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    @Value("${github.prefix}")
    private String githubPrefix;

    @Value("${github.api-url}")
    private String githubApiUrl;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtil tokenUtil;

    public ApiResponse<MemberAddRes> register(MemberAddReq req) {
        // 회원 닉네임 검증
        checkValidGithubUsername(req.getNickname());
        checkUniqueNickname(req.getNickname());
        checkTokenOwnerEqualsNickname(req.getToken(), req.getNickname());

        String securedToken = passwordEncoder.encode(req.getToken());
        req.updateTokenSecurity(securedToken); // DB에 유저의 토큰이 들어갈 때 암호화되도록 조치

        // 회원을 만들어줘야 함
        Member newMember = Member.from(req);
        memberRepository.save(newMember);

        // 응답 던져주기
        MemberAddRes result = MemberAddRes.withNickname(newMember.getNickname());

        return new ApiResponse<>(200, result);
    }

    public TokenDto login(LoginReq req) {
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_USER");

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                req.getNickname(),
                null,
                Collections.singleton(simpleGrantedAuthority)
        );

        setAuthenticationInSecurityContextHolder(authentication);

        Member targetMember = memberRepository.findByNickname(req.getNickname())
                .orElseThrow(() -> new NicknameNotFoundException(NicknameNotFoundException.message));

        // 토큰 원본 컴포넌트에 보관
        tokenUtil.updateToken(req.getToken());

        String jwt = tokenProvider.createToken(authentication, targetMember);
        return TokenDto.withToken(jwt);
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
                restTemplate.exchange(githubApiUrl, HttpMethod.GET, requestEntity, JsonNode.class);
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
        return githubPrefix + username;
    }

    // JwtFilter에서 썼던 표현 재활용
    private static void setAuthenticationInSecurityContextHolder(Authentication authentication) {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
    }
}