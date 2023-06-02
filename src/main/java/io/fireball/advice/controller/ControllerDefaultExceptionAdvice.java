package io.fireball.advice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "io.fireball.controller")
public class ControllerDefaultExceptionAdvice {
    @ExceptionHandler
    public ResponseEntity<String> exceptionHandler(Throwable e) {
        return ResponseEntity.internalServerError()
                .header("message", e.getMessage())
                .build();
    }
}
