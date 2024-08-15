package com.jaret.joke_of_the_day.api;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

// Could use Lombok for getters/setters
public class JokeOfTheDay {
    private Long id;
    private String joke;
    private LocalDate date;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJoke() {
        return joke;
    }

    public void setJoke(String joke) {
        this.joke = joke;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
