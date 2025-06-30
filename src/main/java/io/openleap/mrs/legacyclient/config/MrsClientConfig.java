package io.openleap.mrs.legacyclient.config;

import io.openleap.mrs.client.ApiClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties({MrsClientProperties.class})
public class MrsClientConfig {
    private final MrsClientProperties properties;

    public MrsClientConfig(MrsClientProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ApiClient apiClient(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder
                .build();

        ApiClient client = new ApiClient(restTemplate);
        client.setBasePath(properties.getBaseUrl());

        client.setAccessToken(
                new OAuthTokenSupplier(
                        properties.getTokenUrl(),
                        properties.getClientId(),
                        properties.getClientSecret()
                ));
        return client;
    }
}
