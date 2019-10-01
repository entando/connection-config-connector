package org.entando.connectionconfigconnector;

import java.util.Collections;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;
import org.entando.connectionconfigconnector.model.ConnectionConfig;

@UtilityClass
public class TestHelper {

    public static ConnectionConfig getRandomConnectionConfig() {
        return ConnectionConfig.builder()
                .url(RandomStringUtils.randomAlphabetic(100))
                .name(RandomStringUtils.randomAlphabetic(10))
                .username(RandomStringUtils.randomAlphabetic(10))
                .password(RandomStringUtils.randomAlphabetic(10))
                .serviceType(RandomStringUtils.randomAlphabetic(10))
                .properties(Collections
                        .singletonMap(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10)))
                .build();
    }
}
