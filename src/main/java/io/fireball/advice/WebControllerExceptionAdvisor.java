package io.fireball.advice;

import io.fireball.exception.ServerRuntimeException;
import io.fireball.specification.response.ResponseSpec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.ExecutionException;

@Slf4j
@RestControllerAdvice(basePackages = "io.fireball.controller")
public class WebControllerExceptionAdvisor {
    @ExceptionHandler
    public ResponseEntity<String> exceptionHandler(Throwable exception) {
        log.error(exception.getMessage(), exception);
        var unwrappedException = unwrap(exception);
        var responseSpec = matchSpec(unwrappedException);
        return buildEntity(responseSpec);
    }

    private static Throwable unwrap(Throwable e) {
        if (e instanceof ExecutionException executionException) {
            return e.getCause();
        } else {
            return e;
        }
    }

    private static ResponseSpec matchSpec(Throwable e) {
        if (e instanceof ServerRuntimeException serverRuntimeException) {
            return ResponseSpec.match(serverRuntimeException.getErrorNo());
        } else {
            return ResponseSpec.match(e);
        }
    }

    private static ResponseEntity<String> buildEntity(ResponseSpec responseSpec) {
        return ResponseEntity.status(responseSpec.getHttpResponseStatus())
                .header("Error-No", responseSpec.getErrorNo().toString())
                .header("Error-Message", responseSpec.getErrorMessage())
                .build();
    }
}
