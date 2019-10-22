package org.entando.connectionconfigconnector.service;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Java6JUnitSoftAssertions;
import org.entando.connectionconfigconnector.TestHelper;
import org.entando.connectionconfigconnector.exception.ConnectionNotFoundException;
import org.entando.connectionconfigconnector.exception.InvalidStrictOperationException;
import org.entando.connectionconfigconnector.model.ConnectionConfig;
import org.entando.connectionconfigconnector.model.SecurityLevel;
import org.entando.connectionconfigconnector.service.impl.ConnectionConfigConnectorFileSystem;
import org.entando.connectionconfigconnector.service.impl.ConnectionConfigConnectorImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@SuppressWarnings("PMD.TooManyMethods")
public class ConnectionConfigConnectorStrictTest {

    private static final String FOO_CONFIG_NAME = "foo";
    private static final String BAR_CONFIG_NAME = "bar";
    private static final String TEST_CONFIG_NAME = "test";
    private static final String KEY_1 = "key1";
    private static final String KEY_2 = "key2";
    private static final String VALUE_1 = "value1";
    private static final String VALUE_2 = "value2";

    @Rule
    public TemporaryFolder rootDirectory = new TemporaryFolder();

    @Rule
    public Java6JUnitSoftAssertions safely = new Java6JUnitSoftAssertions();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ConnectionConfigConnector connectionConfigConnector;

    @Before
    public void setUp() {
        ConnectionConfigConnectorFileSystem connectionConfigConnectorFileSystem = new ConnectionConfigConnectorFileSystem(
                rootDirectory.getRoot().getAbsolutePath());
        connectionConfigConnector = new ConnectionConfigConnectorImpl(SecurityLevel.STRICT.toString(),
                mock(RestTemplate.class), connectionConfigConnectorFileSystem);
    }

    @Test
    public void shouldGetConnectionConfigFromFileSystem() throws Exception {
        // Given
        ConnectionConfig configFile = createConfigFile(FOO_CONFIG_NAME);

        // When
        ConnectionConfig connectionConfig = connectionConfigConnector.getConnectionConfig(FOO_CONFIG_NAME);

        safely.assertThat(connectionConfig.getUrl()).isEqualTo(configFile.getUrl());
        safely.assertThat(connectionConfig.getUsername()).isEqualTo(configFile.getUsername());
        safely.assertThat(connectionConfig.getPassword()).isEqualTo(configFile.getPassword());
        safely.assertThat(connectionConfig.getName()).isEqualTo(FOO_CONFIG_NAME);
        safely.assertThat(connectionConfig.getServiceType()).isEqualTo(configFile.getServiceType());
    }

    @Test
    public void shouldThrowConnectionNotFoundException() {
        expectedException.expect(ConnectionNotFoundException.class);
        expectedException.expectMessage(ConnectionNotFoundException.MESSAGE_KEY);

        connectionConfigConnector.getConnectionConfig(FOO_CONFIG_NAME);
    }

    @Test
    public void shouldReturnAllConnectionConfigs() throws Exception {
        // Given
        ConnectionConfig fooConfig = createConfigFile(FOO_CONFIG_NAME);
        ConnectionConfig barConfig = createConfigFile(BAR_CONFIG_NAME);
        ConnectionConfig testConfig = createConfigFile(TEST_CONFIG_NAME);

        // When
        List<ConnectionConfig> connectionConfigs = connectionConfigConnector.getConnectionConfigs();

        // Then
        assertThat(connectionConfigs).containsExactlyInAnyOrder(fooConfig, barConfig, testConfig);
    }

    @Test
    public void shouldReturnEmptyListOnError() {
        // Given
        ConnectionConfigConnectorFileSystem connectionConfigConnectorFileSystem = new ConnectionConfigConnectorFileSystem(
                "/wrong_path");
        connectionConfigConnector = new ConnectionConfigConnectorImpl(SecurityLevel.STRICT.toString(),
                mock(RestTemplate.class), connectionConfigConnectorFileSystem);

        // When
        List<ConnectionConfig> connectionConfigs = connectionConfigConnector.getConnectionConfigs();

        // Then
        assertThat(connectionConfigs).isEmpty();
    }

    @Test
    public void shouldReturnEmptyListForEmptyDirectory() {
        // Given empty root directory
        // When
        List<ConnectionConfig> connectionConfigs = connectionConfigConnector.getConnectionConfigs();

        // Then
        assertThat(connectionConfigs).isEmpty();
    }

    @Test
    public void shouldRetrieveExtraProperties() throws Exception {
        // Given
        ConnectionConfig inputConfig = createConnectionConfig(null);
        inputConfig.getProperties().put(KEY_1, VALUE_1);
        inputConfig.getProperties().put(KEY_2, VALUE_2);
        createConfigFile(FOO_CONFIG_NAME, inputConfig);

        // When
        ConnectionConfig connectionConfig = connectionConfigConnector.getConnectionConfig(FOO_CONFIG_NAME);

        assertThat(connectionConfig.getProperties().size()).isEqualTo(2);
        assertThat(connectionConfig.getProperties().get(KEY_1)).isEqualTo(VALUE_1);
        assertThat(connectionConfig.getProperties().get(KEY_2)).isEqualTo(VALUE_2);
    }

    @Test
    public void shouldRaiseExceptionWhenAddingOnStrictSecurityLevel() {
        expectedException.expect(InvalidStrictOperationException.class);
        expectedException.expectMessage(InvalidStrictOperationException.MESSAGE_KEY);

        ConnectionConfig connectionConfig = TestHelper.getRandomConnectionConfig();
        connectionConfigConnector.addConnectionConfig(connectionConfig);
    }

    @Test
    public void shouldRaiseExceptionWhenDeletingOnStrictSecurityLevel() {
        expectedException.expect(InvalidStrictOperationException.class);
        expectedException.expectMessage(InvalidStrictOperationException.MESSAGE_KEY);

        connectionConfigConnector.deleteConnectionConfig(RandomStringUtils.randomAlphabetic(10));
    }

    @Test
    public void shouldRaiseExceptionWhenEditingOnStrictSecurityLevel() {
        expectedException.expect(InvalidStrictOperationException.class);
        expectedException.expectMessage(InvalidStrictOperationException.MESSAGE_KEY);

        ConnectionConfig connectionConfig = TestHelper.getRandomConnectionConfig();
        connectionConfigConnector.editConnectionConfig(connectionConfig);
    }

    private ConnectionConfig createConfigFile(String configName) throws IOException {
        return createConfigFile(configName, null);
    }

    private ConnectionConfig createConfigFile(String configName, ConnectionConfig connectionConfig) throws IOException {
        File configDirectory = rootDirectory.newFolder(configName);
        ConnectionConfig newConnectionConfig =
                connectionConfig == null ? createConnectionConfig(configName) : connectionConfig;
        Yaml yaml = new Yaml(new Constructor(ConnectionConfig.class));
        String yamlString = yaml.dump(newConnectionConfig);
        Files.write(Paths.get(configDirectory.getAbsolutePath(), "config.yaml"), yamlString.getBytes());
        return newConnectionConfig;
    }

    private ConnectionConfig createConnectionConfig(String configName) {
        return ConnectionConfig.builder()
                .name(configName)
                .url(RandomStringUtils.randomAlphabetic(100))
                .username(RandomStringUtils.randomAlphabetic(20))
                .password(RandomStringUtils.randomAlphabetic(20))
                .serviceType(RandomStringUtils.randomAlphabetic(20))
                .properties(new HashMap<>())
                .build();
    }
}
