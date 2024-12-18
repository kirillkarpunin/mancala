package com.bol.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class ApplicationExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        LOGGER.error("Method argument is not valid", ex);

        var msg = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> "Invalid value in '%s' field: %s".formatted(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));


        return buildResponse(HttpStatus.BAD_REQUEST, msg);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException ex) {
        LOGGER.error("Application exception", ex);
        return buildResponse(ex.getHttpStatus(), ex.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleThrowable(Throwable th) {
        LOGGER.error("Internal server error", th);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, th.getMessage());
    }

    private static ResponseEntity<ErrorResponse> buildResponse(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus)
                .body(new ErrorResponse(message));
    }
}
