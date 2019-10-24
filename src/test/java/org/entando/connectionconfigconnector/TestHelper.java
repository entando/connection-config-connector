package org.entando.connectionconfigconnector;

import com.google.common.collect.ImmutableMap;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;
import org.entando.connectionconfigconnector.model.ConnectionConfig;

@UtilityClass
public class TestHelper {

    public static ConnectionConfig getRandomConnectionConfig() {
        return ConnectionConfig.builder()
                .name(RandomStringUtils.randomAlphabetic(10))
                .properties(ImmutableMap
                        .of(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10),
                                RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10),
                                RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10)))
                .build();
    }
}
