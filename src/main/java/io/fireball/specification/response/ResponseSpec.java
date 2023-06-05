package io.fireball.specification.response;

import io.fireball.exception.ServerNotResponseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.FileNotFoundException;

@RequiredArgsConstructor
@Getter
public enum ResponseSpec {
    // Define common error codes.
    OK (0, "OK", HttpStatus.OK),
    SYSTEM_ERROR(1, "System Error", HttpStatus.INTERNAL_SERVER_ERROR),
    // Define specific error codes.
    FILE_NOT_FOUND (100, "File Not Found", HttpStatus.BAD_REQUEST),
    SERVER_NOT_RESPONSE (101, "The server is not responding.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;
    private final Integer errorNo;
    private final String message;
    private final HttpStatus httpStatus;

    public static ResponseSpec match(int code) {
        return switch(code) {
            case 0 -> OK;
            case 1 -> SYSTEM_ERROR;
            case 100 -> FILE_NOT_FOUND;
            case 101 -> SERVER_NOT_RESPONSE;
            default -> throw new IllegalArgumentException("Unknown code: " + code);
        };
    }

    public static ResponseSpec match(Throwable throwable) {
        if (throwable == null) {
            return OK;
        }

        if (throwable instanceof FileNotFoundException) {
            return FILE_NOT_FOUND;
        } else if (throwable instanceof ServerNotResponseException) {
            return SERVER_NOT_RESPONSE;
        } else {
            return SYSTEM_ERROR;
        }
    }
}
