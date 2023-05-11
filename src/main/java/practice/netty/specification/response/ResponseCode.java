package practice.netty.specification.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResponseCode {
    OK (200, "OK"),
    SYSTEM_ERROR(400, "System Error"),
    IO_ERROR (401, "IO Error"),
    ;
    private final int code;
    private final String message;

    public static ResponseCode match(int code) {
        return switch(code) {
            case 200 -> OK;
            case 400 -> SYSTEM_ERROR;
            case 401 -> IO_ERROR;
            default -> throw new IllegalArgumentException("Unknown code: " + code);
        };
    }

    public static ResponseCode match(Throwable throwable) {
        if (throwable == null) {
            return OK;
        }

        return switch(throwable.getClass().getSimpleName()) {
            case "IOException" -> IO_ERROR;
            default -> SYSTEM_ERROR;
        };
    }
}
