package org.entando.connectionconfigconnector.impl;

import static org.entando.connectionconfigconnector.config.ConnectionConfigConfiguration.CONFIG_REST_TEMPLATE;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.entando.connectionconfigconnector.ConnectionConfigConnector;
import org.entando.connectionconfigconnector.exception.ConnectionConfigException;
import org.entando.connectionconfigconnector.model.ConnectionConfig;
import org.entando.connectionconfigconnector.model.SecurityLevel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@Slf4j
@Service
public class ConnectionConfigConnectorImpl implements ConnectionConfigConnector {

    private static final String CONFIG_YAML = "config.yaml";
    private static final String CONFIG_ENDPOINT = "/config";

    private final String rootDirectory;

    private final SecurityLevel securityLevel;

    private final RestTemplate restTemplate;

    public ConnectionConfigConnectorImpl(
            @Value("${entando.connections.root:/etc/entando/connectionconfigs}") String rootDirectory,
            @Value("${entando.plugin.security.level:STRICT}") String securityLevel,
            @Qualifier(CONFIG_REST_TEMPLATE) RestTemplate restTemplate) {
        this.rootDirectory = rootDirectory;
        this.securityLevel = SecurityLevel.valueOf(securityLevel);
        this.restTemplate = restTemplate;
    }

    @Override
    @SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")
    public Optional<ConnectionConfig> getConnectionConfig(String configName) {
        if (securityLevel == SecurityLevel.STRICT) {
            return getConnectionConfigFromFileSystem(configName);
        } else {
            return getConnectionConfigFromEndpoint(configName);
        }
    }

    private Optional<ConnectionConfig> getConnectionConfigFromFileSystem(String configName) {
        try (InputStream inputStream = Files.newInputStream(Paths.get(rootDirectory, configName, CONFIG_YAML))) {
            Yaml yaml = new Yaml(new Constructor(ConnectionConfig.class));
            ConnectionConfig connectionConfig = yaml.load(inputStream);
            connectionConfig.setName(configName);
            return Optional.of(connectionConfig);
        } catch (IOException e) {
            log.debug("Error retrieving configuration with name {}", configName, e);
            return Optional.empty();
        }
    }

    private Optional<ConnectionConfig> getConnectionConfigFromEndpoint(String configName) {
        try {
            ResponseEntity<ConnectionConfig> response = restTemplate
                    .getForEntity(CONFIG_ENDPOINT + "/" + configName, ConnectionConfig.class);
            return Optional.ofNullable(response.getBody());
        } catch (HttpClientErrorException e) {
            log.debug("Error retrieving configuration with name {}", configName, e);
            return Optional.empty();
        }
    }

    @Override
    public List<ConnectionConfig> getConnectionConfigs() {
        if (securityLevel == SecurityLevel.STRICT) {
            return getConnectionConfigsFromFileSystem();
        } else {
            return getConnectionConfigsFromEndpoint();
        }
    }

    private List<ConnectionConfig> getConnectionConfigsFromFileSystem() {
        try {
            return Files.walk(Paths.get(rootDirectory))
                    .map(Path::toFile)
                    .filter(File::isDirectory)
                    .filter(e -> !e.getAbsolutePath().equals(rootDirectory))
                    .map(File::getName)
                    .map(this::getConnectionConfig)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.debug("Error retrieving all configurations", e);
            return Collections.emptyList();
        }
    }

    private List<ConnectionConfig> getConnectionConfigsFromEndpoint() {
        try {
            ResponseEntity<List<ConnectionConfig>> response = restTemplate
                    .exchange(CONFIG_ENDPOINT, HttpMethod.GET, null,
                            new ParameterizedTypeReference<List<ConnectionConfig>>() {
                            });
            return response.getBody() == null ? Collections.emptyList() : response.getBody();
        } catch (HttpClientErrorException e) {
            log.debug("Error retrieving configurations", e);
            return Collections.emptyList();
        }
    }

    @Override
    public ConnectionConfig addConnectionConfig(ConnectionConfig connectionConfig) {
        try {
            ResponseEntity<ConnectionConfig> response = restTemplate
                    .postForEntity(CONFIG_ENDPOINT, connectionConfig, ConnectionConfig.class);
            return response.getBody();
        } catch (HttpServerErrorException e) {
            log.error("Error adding connection config {}!", connectionConfig.getName(), e);
            throw new ConnectionConfigException(
                    String.format("Error adding connection config %s!", connectionConfig.getName()), e);
        }
    }

    @Override
    public void deleteConnectionConfig(String configName) {
        try {
            restTemplate.delete(CONFIG_ENDPOINT + "/" + configName);
        } catch (HttpServerErrorException e) {
            log.error("Error deleting connection config {}!", configName, e);
            throw new ConnectionConfigException(
                    String.format("Error deleting connection config %s!", configName), e);
        }
    }

    @Override
    public ConnectionConfig editConnectionConfig(ConnectionConfig connectionConfig) {
        try {
            HttpEntity<ConnectionConfig> request = new HttpEntity<>(connectionConfig);
            ResponseEntity<ConnectionConfig> response = restTemplate
                    .exchange(CONFIG_ENDPOINT, HttpMethod.PUT, request, ConnectionConfig.class);
            return response.getBody();
        } catch (HttpServerErrorException e) {
            log.error("Error editing connection config {}!", connectionConfig.getName(), e);
            throw new ConnectionConfigException(
                    String.format("Error editing connection config %s!", connectionConfig.getName()), e);
        }
    }
}
