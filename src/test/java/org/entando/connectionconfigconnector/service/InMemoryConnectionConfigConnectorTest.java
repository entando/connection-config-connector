package org.entando.connectionconfigconnector.service;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.entando.connectionconfigconnector.TestHelper;
import org.entando.connectionconfigconnector.exception.ConnectionAlreadyExistsException;
import org.entando.connectionconfigconnector.exception.ConnectionNotFoundException;
import org.entando.connectionconfigconnector.model.ConnectionConfig;
import org.entando.connectionconfigconnector.service.impl.InMemoryConnectionConfigConnector;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class InMemoryConnectionConfigConnectorTest {

    private ConnectionConfigConnector connectionConfigConnector;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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

        assertThat(connectionConfigConnector.getConnectionConfig(config1.getName())).isEqualTo(config1);
        assertThat(connectionConfigConnector.getConnectionConfig(config2.getName())).isEqualTo(config2);
        assertThat(connectionConfigConnector.getConnectionConfig(config3.getName())).isEqualTo(config3);
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

        assertThat(connectionConfigConnector.getConnectionConfig(connectionConfig.getName()))
                .isEqualTo(editedConnectionConfig);
    }

    @Test
    public void shouldThrowConnectionNotFoundExceptionWhenGetting() {
        expectedException.expect(ConnectionNotFoundException.class);
        expectedException.expectMessage(ConnectionNotFoundException.MESSAGE_KEY);

        connectionConfigConnector.getConnectionConfig("invalid");
    }

    @Test
    public void shouldThrowConnectionNotFoundExceptionWhenEditing() {
        expectedException.expect(ConnectionNotFoundException.class);
        expectedException.expectMessage(ConnectionNotFoundException.MESSAGE_KEY);

        ConnectionConfig connectionConfig = TestHelper.getRandomConnectionConfig();
        connectionConfigConnector.editConnectionConfig(connectionConfig);
    }

    @Test
    public void shouldThrowConnectionNotFoundExceptionWhenDeleting() {
        expectedException.expect(ConnectionNotFoundException.class);
        expectedException.expectMessage(ConnectionNotFoundException.MESSAGE_KEY);

        connectionConfigConnector.deleteConnectionConfig("invalid");
    }

    @Test
    public void shouldThrowConnectionAlreadyExistsExceptionWhenAddingDuplicate() {
        expectedException.expect(ConnectionAlreadyExistsException.class);
        expectedException.expectMessage(ConnectionAlreadyExistsException.MESSAGE_KEY);

        ConnectionConfig connectionConfig = TestHelper.getRandomConnectionConfig();
        connectionConfigConnector.addConnectionConfig(connectionConfig);
        connectionConfigConnector.addConnectionConfig(connectionConfig);
    }
}
