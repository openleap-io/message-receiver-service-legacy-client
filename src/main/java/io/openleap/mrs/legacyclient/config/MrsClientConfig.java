package io.openleap.mrs.legacyclient.config;

import io.openleap.mrs.client.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Configuration
@EnableConfigurationProperties({MrsClientProperties.class})
public class MrsClientConfig {
    Logger logger = LoggerFactory.getLogger(MrsClientConfig.class);
    private final MrsClientProperties properties;

    public MrsClientConfig(MrsClientProperties properties) {
        this.properties = properties;
        init();
    }


    String baseUrl;
    String tokenUrl;
    String clientId;
    String clientSecret;

    public void init() {
        String mailSendConfig = System.getenv("MAILSEND_CONFIG");
        if (mailSendConfig != null && (new File(mailSendConfig)).exists()) {
            File configFile = new File(mailSendConfig);
            logger.info("mail send config file detected: {}", mailSendConfig);
            try {
                String cfg = new String(java.nio.file.Files.readAllBytes(configFile.toPath()), java.nio.charset.StandardCharsets.ISO_8859_1);
                String[] lines = cfg.split("\\R");
                for (String line : lines) {
                    line = line.trim().replace(" ", "");
                    if (line.startsWith("base-url:")) {
                        this.baseUrl = line.split("base-url:")[1];
                    } else if (line.startsWith("token-url:")) {
                        this.tokenUrl = line.split("token-url:")[1];
                    } else if (line.startsWith("client-id:")) {
                        this.clientId = line.split("client-id:")[1];
                    } else if (line.startsWith("client-secret:")) {
                        this.clientSecret = line.split("client-secret:")[1];
                    }
                }
            } catch (Exception e) {
                logger.error("Error reading mail send config file: {}", e.getMessage());
            }

        } else {
            logger.info("mail send config file not found, using default values");
            this.baseUrl = properties.getBaseUrl();
            this.tokenUrl = properties.getTokenUrl();
            this.clientId = properties.getClientId();
            this.clientSecret = properties.getClientSecret();
        }
    }

    @Bean
    public ApiClient apiClient(RestTemplateBuilder builder) {


        RestTemplate restTemplate = builder
                .build();

        ApiClient client = new ApiClient(restTemplate);
        client.setBasePath(baseUrl);

        client.setAccessToken(
                new OAuthTokenSupplier(
                        tokenUrl,
                        clientId,
                        clientSecret
                ));
        return client;
    }
}
