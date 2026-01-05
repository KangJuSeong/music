package com.example.music.exception;

import com.example.music.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponseDto>> handleServerError(Exception e) {
        log.error("Exception ", e);
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                LocalDateTime.now(),
                e.toString()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDto));
    }
}
