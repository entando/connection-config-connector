package org.entando.connectionconfigconnector.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConnectionConfig {

    private String url;
    private String username;
    private String password;
    private String name;
    private String serviceType;
    private Map<String, String> properties;
}
