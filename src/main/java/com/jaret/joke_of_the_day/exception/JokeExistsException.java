package com.jaret.joke_of_the_day.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="No such Joke")
public class JokeExistsException extends RuntimeException {
    public JokeExistsException() {
        super();
    }

    public JokeExistsException(String message) {
        super(message);
    }

    public JokeExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public JokeExistsException(Throwable cause) {
        super(cause);
    }

    protected JokeExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}