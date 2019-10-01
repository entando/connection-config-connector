package org.entando.connectionconfigconnector.exception;

public class ConnectionConfigException extends RuntimeException {

    public ConnectionConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConnectionConfigException(String message) {
        super(message);
    }
}
