package com.jaret.joke_of_the_day.service;

import com.jaret.joke_of_the_day.api.JokeOfTheDay;

import java.time.LocalDate;
import java.util.List;

public interface JokeOfTheDaySourceInterface {
    JokeOfTheDay getJokeOfTheDayById(Long id);
    JokeOfTheDay getJokeOfTheDayForToday();
    JokeOfTheDay getJokeOfTheDayForDate(LocalDate date);
    JokeOfTheDay createJokeOfTheDay(JokeOfTheDay jokeOfTheDay);
    JokeOfTheDay updateJokeOfTheDay(JokeOfTheDay jokeOfTheDay, Long id);
    void deleteJokeOfTheDay(Long id);
    List<JokeOfTheDay> getAllJokesOfTheDay();
}
