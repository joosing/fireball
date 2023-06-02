package io.fireball.advice.controller;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.FileNotFoundException;

@RestControllerAdvice(basePackages = "io.fireball.controller")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ControllerFileNotFoundExceptionAdvice {
    @ExceptionHandler
    public ResponseEntity<String> handler(FileNotFoundException e) {
        return ResponseEntity.notFound()
                .header("message", e.getMessage())
                .build();
    }
}
