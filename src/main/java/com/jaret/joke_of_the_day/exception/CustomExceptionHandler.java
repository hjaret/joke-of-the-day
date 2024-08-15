package com.jaret.joke_of_the_day.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLSyntaxErrorException;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {BadRequestException.class})
    protected ResponseEntity<Object> handleBadRequest(BadRequestException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        logger.error(bodyOfResponse, ex);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {SQLSyntaxErrorException.class})
    protected ResponseEntity<Object> handleJdbcException(SQLSyntaxErrorException ex, WebRequest request) {
        String bodyOfResponse = "There was a SQL syntax error";
        logger.error(bodyOfResponse, ex);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {JokeNotFoundException.class})
    protected ResponseEntity<Object> handleJokeNotFoundException(JokeNotFoundException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        logger.error(bodyOfResponse, ex);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(value = {InvalidJokeException.class})
    protected ResponseEntity<Object> handleInvalidJokeException(InvalidJokeException ex, WebRequest request) {
        String bodyOfResponse = ex.toString();
        logger.error(bodyOfResponse, ex);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {JokeExistsException.class})
    protected ResponseEntity<Object> handleJokeExistsException(JokeExistsException ex, WebRequest request) {
        String bodyOfResponse = ex.getMessage();
        logger.error(bodyOfResponse, ex);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {JdbcSQLIntegrityConstraintViolationException.class, DataIntegrityViolationException.class})
    protected ResponseEntity<Object> handleJokeExistsException(JdbcSQLIntegrityConstraintViolationException ex, WebRequest request) {
        String bodyOfResponse = "Only one joke can be created for a specific date";
        logger.error(bodyOfResponse, ex);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleJokeExistsException(Exception ex, WebRequest request) {
        String bodyOfResponse = "Unknown error occurred";
        logger.error(bodyOfResponse, ex);
        return handleExceptionInternal(ex, bodyOfResponse,
                new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
