package com.itembase.currencyconverter.controller;

import com.itembase.currencyconverter.domain.dto.Error;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class ConversionErrorHandler {
    private static final String INTERNAL_ERROR_KEY = "internalError";
    private static final String GENERAL_ERROR_MESSAGE = "An error occurred while processing the request, {}";

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Error> handleIllegalArgumentException(final IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(buildErrorResponse(BAD_REQUEST.value(), exception.getMessage(), exception));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Error> handleIllegalStateException(final IllegalStateException exception) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(INTERNAL_SERVER_ERROR.value(), exception.getMessage(), exception));
    }

    private Error buildErrorResponse(final int status, final String message, final Exception exception) {
        log.error(GENERAL_ERROR_MESSAGE, message, exception);
        final Error error = new Error();
        error.setCode(status);
        error.setErrorMessage(message);
        return error;
    }

}
