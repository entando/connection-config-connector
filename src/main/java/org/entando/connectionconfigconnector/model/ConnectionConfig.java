package org.entando.connectionconfigconnector.model;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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
