package com.jaret.joke_of_the_day.exception;

import org.springframework.http.HttpStatus;

import java.util.List;

public class ApiError {
    private HttpStatus status;
    private String message;
    private String error;
}
