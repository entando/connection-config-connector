package org.entando.connectionconfigconnector;

import java.util.List;
import java.util.Optional;
import org.entando.connectionconfigconnector.model.ConnectionConfig;

public interface ConnectionConfigConnector {

    Optional<ConnectionConfig> getConnectionConfig(String configName);

    List<ConnectionConfig> getConnectionConfigs();

    ConnectionConfig addConnectionConfig(ConnectionConfig connectionConfig);

    void deleteConnectionConfig(String configName);

    ConnectionConfig editConnectionConfig(ConnectionConfig connectionConfig);
}
