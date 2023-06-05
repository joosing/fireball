package io.fireball.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class ServerRuntimeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1375832192051626471L;
    private final Integer errorNo;
    public ServerRuntimeException(Integer errorNo, String message) {
        super(message);
        this.errorNo = errorNo;
    }
}
