# Entando Connection Config Connector

This library is used to integrate with the Entando Connection Config Sidecar. The sidecar exposes an API to handle
connection configuration and this library make it possible to integrate with the sidecar without the need to 
implement the communication to the service.

## Install

Add the following dependency to your project:
```xml
<dependency>
    <groupId>org.entando</groupId>
    <artifactId>connection-config-connector</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

The library includes the configuration class `org.entando.connectionconfigconnector.config.ConnectionConfigConfiguration`
and you need to make sure it will be loaded. One way to do that is by using the `@Import` Spring annotation:
```java
@SpringBootApplication
@Import(ConnectionConfigConfiguration.class)
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
```

## Security Level

This library has two modes or security levels: STRICT and LENIENT. You can choose the security level using the spring
property `entando.plugin.security.level`.

### STRICT

This is the default security level. On this level, the library does not communicate with the sidecar. Hence, the sidecar
does not need to be deployed when using this level.

On the Strict security level, only read operations can be performed and the exception `ConnectionConfigException` is 
raised when trying to perform other operations (add, delete, edit).

When running on Strict security level, connection config is read from the file system and the root directory is defined
by the spring property `entando.connections.root`, which has the default value `/etc/entando/connectionconfigs`. Since
only read operations are allowed on the strict mode, the files with the configurations should be included manually.
Inside the root directory, there should be one subdirectory for each connection config and each subdirectory must have
a file named `config.yaml`. The yaml file should contain attributes matching the class `ConnectionConfig.java`:
```yaml
name: <connection_name>
url: <connection_url>
username: <connection_username>
password: <connection_password>
serviceType: <connection_serviceType>
properties:
  property1: value1
  property2: value2
```

### LENIENT

On this security level the library communicate with the sidecar, so the sidecar needs to be deployed and reachable
through localhost.

When using Lenient security level, all operations are supported and translated to HTTP requests on localhost on 
the port number defined by the Spring property `plugin.sidecar.port`, which has the default value 8084. This is where
the sidecar must be running.

To get more information regarding the sidecar, please check its repository:
https://github.com/entando/entando-plugin-sidecar
