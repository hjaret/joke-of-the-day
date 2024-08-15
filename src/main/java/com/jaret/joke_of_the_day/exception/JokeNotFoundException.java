package com.jaret.joke_of_the_day.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such Joke")
public class JokeNotFoundException extends RuntimeException {
    public JokeNotFoundException() {
        super();
    }

    public JokeNotFoundException(String message) {
        super(message);
    }

    public JokeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public JokeNotFoundException(Throwable cause) {
        super(cause);
    }

    protected JokeNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}