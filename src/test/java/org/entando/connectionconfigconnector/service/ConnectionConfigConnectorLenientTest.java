package org.entando.connectionconfigconnector.service;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.entando.connectionconfigconnector.TestHelper;
import org.entando.connectionconfigconnector.config.TestConnectionConfigConfiguration;
import org.entando.connectionconfigconnector.exception.ConnectionConfigException;
import org.entando.connectionconfigconnector.model.ConnectionConfig;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConnectionConfigConfiguration.class, properties = "entando.plugin.security.level=LENIENT")
@SuppressWarnings("PMD.TooManyMethods")
public class ConnectionConfigConnectorLenientTest {

    private static final String ENDPOINT = "http://localhost:8084/config";

    @Autowired
    private ConnectionConfigConnector connectionConfigConnector;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;
    private final ObjectMapper mapper = new ObjectMapper();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void shouldGetConnectionConfigFromEndpoint() throws Exception {
        // Given
        ConnectionConfig connectionConfig = TestHelper.getRandomConnectionConfig();
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(ENDPOINT + "/" + connectionConfig.getName())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(connectionConfig)));

        // When
        Optional<ConnectionConfig> fromServer = connectionConfigConnector
                .getConnectionConfig(connectionConfig.getName());

        // Then
        mockServer.verify();
        assertThat(fromServer.isPresent()).isTrue();
        assertThat(fromServer.get()).isEqualTo(connectionConfig);
    }

    @Test
    public void shouldReturnEmptyForStatusCodeWithError() throws Exception {
        // Given
        ConnectionConfig connectionConfig = TestHelper.getRandomConnectionConfig();
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(ENDPOINT + "/" + connectionConfig.getName())))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // When
        Optional<ConnectionConfig> fromServer = connectionConfigConnector
                .getConnectionConfig(connectionConfig.getName());

        // Then
        mockServer.verify();
        assertThat(fromServer.isPresent()).isFalse();
    }

    @Test
    public void shouldGetAllConnectionConfigsFromEndpoint() throws Exception {
        // Given
        ConnectionConfig config1 = TestHelper.getRandomConnectionConfig();
        ConnectionConfig config2 = TestHelper.getRandomConnectionConfig();
        ConnectionConfig config3 = TestHelper.getRandomConnectionConfig();
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(ENDPOINT)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(Arrays.asList(config1, config2, config3))));

        // When
        List<ConnectionConfig> connectionConfigs = connectionConfigConnector.getConnectionConfigs();

        // Then
        mockServer.verify();
        assertThat(connectionConfigs).containsExactlyInAnyOrder(config1, config2, config3);
    }

    @Test
    public void shouldReturnEmptyListForErrorWhenGettingAllConfigurations() throws Exception {
        // Given
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(ENDPOINT)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // When
        List<ConnectionConfig> connectionConfigs = connectionConfigConnector.getConnectionConfigs();

        // Then
        mockServer.verify();
        assertThat(connectionConfigs).isEmpty();
    }

    @Test
    public void shouldAddConnectionConfigToEndpoint() throws Exception {
        // Given
        ConnectionConfig connectionConfig = TestHelper.getRandomConnectionConfig();
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(ENDPOINT)))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(mapper.writeValueAsString(connectionConfig)))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(connectionConfig)));

        // When
        ConnectionConfig fromServer = connectionConfigConnector.addConnectionConfig(connectionConfig);

        // Then
        mockServer.verify();
        assertThat(fromServer).isEqualTo(connectionConfig);
    }

    @Test
    public void shouldThrowExceptionForErrorWhenAddingConnectionConfig() throws Exception {
        expectedException.expect(ConnectionConfigException.class);

        ConnectionConfig connectionConfig = TestHelper.getRandomConnectionConfig();
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(ENDPOINT)))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(mapper.writeValueAsString(connectionConfig)))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        connectionConfigConnector.addConnectionConfig(connectionConfig);

        mockServer.verify();
    }

    @Test
    public void shouldDeleteConnectionConfigUsingEndpoint() throws Exception {
        // Given
        ConnectionConfig connectionConfig = TestHelper.getRandomConnectionConfig();
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(ENDPOINT + "/" + connectionConfig.getName())))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.OK));

        // When
        connectionConfigConnector.deleteConnectionConfig(connectionConfig.getName());

        // Then
        mockServer.verify();
    }

    @Test
    public void shouldThrowExceptionForErrorWhenDeletingConnectionConfig() throws Exception {
        expectedException.expect(ConnectionConfigException.class);

        ConnectionConfig connectionConfig = TestHelper.getRandomConnectionConfig();
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(ENDPOINT + "/" + connectionConfig.getName())))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        connectionConfigConnector.deleteConnectionConfig(connectionConfig.getName());

        mockServer.verify();
    }

    @Test
    public void shouldEditConnectionConfigUsingEndpoint() throws Exception {
        // Given
        ConnectionConfig connectionConfig = TestHelper.getRandomConnectionConfig();
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(ENDPOINT)))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().json(mapper.writeValueAsString(connectionConfig)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(connectionConfig)));

        // When
        ConnectionConfig fromServer = connectionConfigConnector.editConnectionConfig(connectionConfig);

        // Then
        mockServer.verify();
        assertThat(fromServer).isEqualTo(connectionConfig);
    }

    @Test
    public void shouldThrowExceptionForErrorWhenEditingConnectionConfig() throws Exception {
        expectedException.expect(ConnectionConfigException.class);

        ConnectionConfig connectionConfig = TestHelper.getRandomConnectionConfig();
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(ENDPOINT)))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(content().json(mapper.writeValueAsString(connectionConfig)))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        connectionConfigConnector.editConnectionConfig(connectionConfig);

        mockServer.verify();
    }
}
