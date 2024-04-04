package dk.digitalidentity.ap.service.kitos.auth;

import dk.digitalidentity.ap.service.kitos.auth.api.TokenRequest;
import dk.digitalidentity.ap.service.kitos.auth.api.TokenResponse;
import dk.digitalidentity.ap.service.kitos.auth.exception.KitosAuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;

@Service
public class AuthTokenService {
    private final RestTemplate restTemplate;
    private String token;
    private OffsetDateTime tokenExpires;

    @Value("${kitos.base.url}")
    private String kitosUrl;

    @Value("${kitos.rest.username}")
    private String kitosUsername;

    @Value("${kitos.rest.password}")
    private String kitosPassword;

    static final Integer AUTH_S_BEFORE_EXPIRE = 30;

    public AuthTokenService(final RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String getAuthToken() {
        if (tokenExpires == null || tokenExpires.minusSeconds(AUTH_S_BEFORE_EXPIRE).isBefore(OffsetDateTime.now())) {
            final TokenResponse tokenResponse = fetchTokenResponse();
            token = tokenResponse.getResponse().getToken();
            tokenExpires = tokenResponse.getResponse().getExpires();
        }
        return token;
    }

    private TokenResponse fetchTokenResponse() {
        String basePath = kitosUrl;
        final TokenRequest tokenRequest = new TokenRequest(
            kitosUsername,
            kitosPassword
        );
        final HttpEntity<TokenRequest> request = new HttpEntity<>(tokenRequest);
        final ResponseEntity<TokenResponse> response =
            restTemplate.postForEntity(basePath + "/api/authorize/gettoken", request, TokenResponse.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new KitosAuthException(response.getStatusCode());
        }
        return response.getBody();
    }

}
