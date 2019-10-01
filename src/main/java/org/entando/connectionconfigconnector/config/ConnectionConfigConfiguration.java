package org.entando.connectionconfigconnector.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan(basePackages = "org.entando.connectionconfigconnector")
public class ConnectionConfigConfiguration {

    public static final String CONFIG_REST_TEMPLATE = "connectionConfigRestTemplate";

    private final String sidecarPort;

    public ConnectionConfigConfiguration(@Value("${plugin.sidecar.port:8084}") String sidecarPort) {
        this.sidecarPort = sidecarPort;
    }

    @Bean
    @Qualifier(CONFIG_REST_TEMPLATE)
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .rootUri("http://localhost:" + sidecarPort)
                .build();
    }
}
