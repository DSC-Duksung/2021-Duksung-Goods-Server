package com.example.duksunggoodsserver.exception;

import com.example.duksunggoodsserver.config.responseEntity.ErrorResponse;
import com.example.duksunggoodsserver.config.responseEntity.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    // 400
    @ExceptionHandler({ RuntimeException.class })
    public ResponseEntity badRequestException(final RuntimeException e) {
        log.warn("error", e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(e.getMessage())
                .build();
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity methodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("error", e);

        StringBuilder message = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            message.append("[");
            message.append(fieldError.getField());
            message.append("]에 ");
            message.append(fieldError.getDefaultMessage());
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message.toString())
                .build();
        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    // 401
    @ExceptionHandler({ AccessDeniedException.class })
    public ResponseEntity handleAccessDeniedException(final AccessDeniedException e) {
        log.warn("error", e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(StatusEnum.UNAUTHORIZED)
                .message(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }

    // 500
    @ExceptionHandler({ Exception.class })
    public ResponseEntity handleAll(final Exception e) {
        log.info(e.getClass().getName());
        log.error("error", e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(StatusEnum.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .build();
        return ResponseEntity.internalServerError()
                .body(errorResponse);
    }
}