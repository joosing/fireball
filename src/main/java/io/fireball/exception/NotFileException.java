package io.fireball.exception;

import java.io.Serial;

public class NotFileException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 5961718089138605963L;

    public NotFileException(String message) {
        super(message);
    }
}
