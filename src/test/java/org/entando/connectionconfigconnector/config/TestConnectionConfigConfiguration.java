package org.entando.connectionconfigconnector.config;

import static org.entando.connectionconfigconnector.config.ConnectionConfigConfiguration.CONFIG_REST_TEMPLATE;

import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootConfiguration
@ComponentScan(basePackages = "org.entando.connectionconfigconnector.service",
        basePackageClasses = KeycloakSecurityComponents.class)
public class TestConnectionConfigConfiguration {

    @Bean
    @Qualifier(CONFIG_REST_TEMPLATE)
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        RootUriTemplateHandler.addTo(restTemplate, "http://localhost:8084");
        return restTemplate;
    }
}
