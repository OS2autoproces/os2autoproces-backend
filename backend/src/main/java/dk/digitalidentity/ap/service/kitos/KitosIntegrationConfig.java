package dk.digitalidentity.ap.service.kitos;

import dk.digitalidentity.ap.service.kitos.auth.AuthTokenService;
import dk.kitos.api.ApiV2DeltaFeedApi;
import dk.kitos.api.ApiV2ItContractApi;
import dk.kitos.api.ApiV2ItSystemApi;
import dk.kitos.api.ApiV2ItSystemUsageApi;
import dk.kitos.api.ApiV2ItSystemUsageRoleTypeApi;
import dk.kitos.api.ApiV2OrganizationApi;
import dk.kitos.client.ApiClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

@Configuration
public class KitosIntegrationConfig {

    @Value("${kitos.base.url}")
    private String kitosUrl;

    @Bean("kitosRestTemplate")
    public RestTemplate restTemplate(final RestTemplateBuilder restTemplateBuilder, final AuthTokenService tokenService) {
        return restTemplateBuilder.additionalInterceptors((httpRequest, bytes, clientHttpRequestExecution) -> {
            httpRequest.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + tokenService.getAuthToken());
            return clientHttpRequestExecution.execute(httpRequest, bytes);
        }).build();
    }

    @Bean
    public ApiClient apiClient(@Qualifier("kitosRestTemplate")  final RestTemplate kitosRestTemplate) {
        final ApiClient apiClient = new ApiClient(kitosRestTemplate);
        apiClient.setBasePath(kitosUrl);
        return apiClient;
    }

    @Bean
    public ApiV2DeltaFeedApi deltaFeedApi(final ApiClient apiClient) {
        return new ApiV2DeltaFeedApi(apiClient);
    }

    @Bean
    public ApiV2ItSystemApi itSystemApi(final ApiClient apiClient) {
        return new ApiV2ItSystemApi(apiClient);
    }

    @Bean
    public ApiV2OrganizationApi organizationApi(final ApiClient apiClient) {
        return new ApiV2OrganizationApi(apiClient);
    }

    @Bean
    public ApiV2ItSystemUsageApi itSystemUsageApi(final ApiClient apiClient) {
        return new ApiV2ItSystemUsageApi(apiClient);
    }

    @Bean
    public ApiV2ItSystemUsageRoleTypeApi itSystemUsageRoleTypeApi(final ApiClient apiClient) {
        return new ApiV2ItSystemUsageRoleTypeApi(apiClient);
    }

    @Bean
    public ApiV2ItContractApi itContractApi(final ApiClient apiClient) {
        return new ApiV2ItContractApi(apiClient);
    }

}
