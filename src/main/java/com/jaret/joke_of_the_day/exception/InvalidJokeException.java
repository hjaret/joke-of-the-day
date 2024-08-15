package com.jaret.joke_of_the_day.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Joke is invalid")
public class InvalidJokeException extends RuntimeException {

    private final List<String> errors;

    public InvalidJokeException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getMessage()).append("\n");
        for(String e : errors) {
            sb.append(e).append("\n");
        }
        return sb.toString();
    }
}