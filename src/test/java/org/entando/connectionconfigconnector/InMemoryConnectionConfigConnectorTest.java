package org.entando.connectionconfigconnector;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.entando.connectionconfigconnector.impl.InMemoryConnectionConfigConnector;
import org.entando.connectionconfigconnector.model.ConnectionConfig;
import org.junit.Before;
import org.junit.Test;

public class InMemoryConnectionConfigConnectorTest {

    private ConnectionConfigConnector connectionConfigConnector;

    @Before
    public void init() {
        connectionConfigConnector = new InMemoryConnectionConfigConnector();
    }

    @Test
    public void shouldAddConnectionConfig() {
        ConnectionConfig connectionConfig = TestHelper.getRandomConnectionConfig();

        connectionConfigConnector.addConnectionConfig(connectionConfig);

        assertThat(connectionConfigConnector.getConnectionConfigs()).contains(connectionConfig);
    }

    @Test
    public void shouldGetConnectionConfigByName() {
        ConnectionConfig config1 = TestHelper.getRandomConnectionConfig();
        ConnectionConfig config2 = TestHelper.getRandomConnectionConfig();
        ConnectionConfig config3 = TestHelper.getRandomConnectionConfig();
        connectionConfigConnector.addConnectionConfig(config1);
        connectionConfigConnector.addConnectionConfig(config2);
        connectionConfigConnector.addConnectionConfig(config3);

        assertThat(connectionConfigConnector.getConnectionConfig(config1.getName()).get()).isEqualTo(config1);
        assertThat(connectionConfigConnector.getConnectionConfig(config2.getName()).get()).isEqualTo(config2);
        assertThat(connectionConfigConnector.getConnectionConfig(config3.getName()).get()).isEqualTo(config3);
    }

    @Test
    public void shouldGetAllConnectionConfigs() {
        ConnectionConfig config1 = TestHelper.getRandomConnectionConfig();
        ConnectionConfig config2 = TestHelper.getRandomConnectionConfig();
        ConnectionConfig config3 = TestHelper.getRandomConnectionConfig();
        connectionConfigConnector.addConnectionConfig(config1);
        connectionConfigConnector.addConnectionConfig(config2);
        connectionConfigConnector.addConnectionConfig(config3);

        assertThat(connectionConfigConnector.getConnectionConfigs())
                .containsExactlyInAnyOrder(config1, config2, config3);
    }

    @Test
    public void shouldDeleteConnectionConfig() {
        ConnectionConfig connectionConfig = TestHelper.getRandomConnectionConfig();
        connectionConfigConnector.addConnectionConfig(connectionConfig);

        connectionConfigConnector.deleteConnectionConfig(connectionConfig.getName());

        assertThat(connectionConfigConnector.getConnectionConfigs()).isEmpty();
    }

    @Test
    public void shouldEditConnectionConfig() {
        ConnectionConfig connectionConfig = TestHelper.getRandomConnectionConfig();
        connectionConfigConnector.addConnectionConfig(connectionConfig);

        ConnectionConfig editedConnectionConfig = TestHelper.getRandomConnectionConfig();
        editedConnectionConfig.setName(connectionConfig.getName());
        connectionConfigConnector.editConnectionConfig(editedConnectionConfig);

        assertThat(connectionConfigConnector.getConnectionConfig(connectionConfig.getName()).get())
                .isEqualTo(editedConnectionConfig);
    }
}
