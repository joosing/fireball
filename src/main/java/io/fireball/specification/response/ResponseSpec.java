package io.fireball.specification.response;

import io.fireball.exception.NotFileException;
import io.fireball.exception.ServerNotResponseException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.FileNotFoundException;

@RequiredArgsConstructor
@Getter
public enum ResponseSpec {
    OK (0, "OK.", HttpStatus.OK),
    FILE_NOT_FOUND (4000, "The file does not exist.", HttpStatus.BAD_REQUEST),
    NOT_FILE (4001, "Item is not a file.", HttpStatus.BAD_REQUEST),
    SYSTEM_ERROR(5000, "Internal system error.", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVER_NOT_RESPONSE (5001, "No response from server.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final Integer errorNo;
    private final String errorMessage;
    private final HttpStatus httpResponseStatus;

    /**
     * Map an errorNo to a ResponseSpec.
     * @param errorNo Error Number
     * @return Matched ResponseSpec
     */
    public static ResponseSpec match(int errorNo) {
        return switch(errorNo) {
            case 0 -> OK;
            case 4000 -> FILE_NOT_FOUND;
            case 4001 -> NOT_FILE;
            case 5000 -> SYSTEM_ERROR;
            case 5001 -> SERVER_NOT_RESPONSE;
            default -> throw new IllegalArgumentException("Unknown errorNo: " + errorNo);
        };
    }

    /**
     * Map an exception to a ResponseSpec.
     * @param throwable Exception
     * @return Matched ResponseSpec
     */
    public static ResponseSpec match(Throwable throwable) {
        if (throwable == null) {
            return OK;
        }

        if (throwable instanceof FileNotFoundException) {
            return FILE_NOT_FOUND;
        } else if (throwable instanceof ServerNotResponseException) {
            return SERVER_NOT_RESPONSE;
        } else if (throwable instanceof NotFileException) {
            return NOT_FILE;
        } else {
            return SYSTEM_ERROR;
        }
    }
}
