package io.fireball.specification.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResponseCode {
    // Define common error codes.
    OK (0, "OK"),
    SYSTEM_ERROR(1, "System Error"),
    // Define specific error codes.
    FILE_NOT_FOUND (100, "File Not Found"),
    ;
    private final int code;
    private final String message;

    public static ResponseCode match(int code) {
        return switch(code) {
            case 0 -> OK;
            case 1 -> SYSTEM_ERROR;
            case 100 -> FILE_NOT_FOUND;
            default -> throw new IllegalArgumentException("Unknown code: " + code);
        };
    }

    public static ResponseCode match(Throwable throwable) {
        if (throwable == null) {
            return OK;
        }

        return switch(throwable.getClass().getSimpleName()) {
            case "FileNotFoundException" -> FILE_NOT_FOUND;
            default -> SYSTEM_ERROR;
        };
    }
}
