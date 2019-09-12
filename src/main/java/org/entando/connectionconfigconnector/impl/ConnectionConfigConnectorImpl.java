package org.entando.connectionconfigconnector.impl;

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
import org.entando.connectionconfigconnector.model.ConnectionConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@Slf4j
@Service
public class ConnectionConfigConnectorImpl implements ConnectionConfigConnector {

    private static final String CONFIG_YAML = "config.yaml";

    private final String rootDirectory;

    public ConnectionConfigConnectorImpl(@Value("${secret.root.directory:/etc/entando/secrets}") String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    @SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")
    public Optional<ConnectionConfig> getConnectionConfig(String configName) {
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

    @Override
    public List<ConnectionConfig> getConnectionConfigs() {
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
}
