package org.entando.connectionconfigconnector.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.entando.connectionconfigconnector.model.ConnectionConfig;
import org.entando.connectionconfigconnector.service.ConnectionConfigConnector;

public class InMemoryConnectionConfigConnector implements ConnectionConfigConnector {

    private final Map<String, ConnectionConfig> connectionConfigMap = new ConcurrentHashMap<>();

    @Override
    public Optional<ConnectionConfig> getConnectionConfig(String configName) {
        return Optional.ofNullable(connectionConfigMap.get(configName));
    }

    @Override
    public List<ConnectionConfig> getConnectionConfigs() {
        return new ArrayList<>(connectionConfigMap.values());
    }

    @Override
    public ConnectionConfig addConnectionConfig(ConnectionConfig connectionConfig) {
        return connectionConfigMap.put(connectionConfig.getName(), connectionConfig);
    }

    @Override
    public void deleteConnectionConfig(String configName) {
        connectionConfigMap.remove(configName);
    }

    @Override
    public ConnectionConfig editConnectionConfig(ConnectionConfig connectionConfig) {
        return connectionConfigMap.put(connectionConfig.getName(), connectionConfig);
    }
}
