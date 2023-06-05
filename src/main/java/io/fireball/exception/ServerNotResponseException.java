package io.fireball.exception;

import java.io.Serial;


public class ServerNotResponseException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1559816505620023965L;

    public ServerNotResponseException() {
        super("The server is not responding.");
    }
}
