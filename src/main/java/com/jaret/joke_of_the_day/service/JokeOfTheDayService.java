package com.jaret.joke_of_the_day.service;

import com.jaret.joke_of_the_day.api.JokeOfTheDay;
import com.jaret.joke_of_the_day.entity.JokeOfTheDayEntity;
import com.jaret.joke_of_the_day.exception.InvalidJokeException;
import com.jaret.joke_of_the_day.exception.JokeNotFoundException;
import com.jaret.joke_of_the_day.repository.JokeOfTheDayRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class JokeOfTheDayService implements JokeOfTheDaySourceInterface {

    private static final String NULL_DATE_MSG = "Date field cannot be null";
    private static final String NULL_JOKE_MSG = "Joke field cannot be null";

    private final JokeOfTheDayRepository jokeOfTheDayRepository;
    private final DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public JokeOfTheDayService(JokeOfTheDayRepository jokeOfTheDayRepository) {
        this.jokeOfTheDayRepository = jokeOfTheDayRepository;
    }

    public JokeOfTheDay getJokeOfTheDayById(Long id) {
        JokeOfTheDayEntity jokeOfTheDayEntity = jokeOfTheDayRepository.findById(id).orElseThrow(() ->
                new JokeNotFoundException(String.format("Joke with id %s was not found", id)));
        JokeOfTheDay newJokeOfTheDay = new JokeOfTheDay();
        modelMapper.map(jokeOfTheDayEntity, newJokeOfTheDay);
        return newJokeOfTheDay;
    }

    /**
     * Get today's joke from the database
     * @return JOTD API object
     */
    public JokeOfTheDay getJokeOfTheDayForToday() {
        return jokeOfTheDayRepository.findForToday().map(jokeOfTheDayEntity -> {
            JokeOfTheDay joke = new JokeOfTheDay();
            modelMapper.map(jokeOfTheDayEntity, joke);
            return joke;
        }).orElseGet(() -> {
            JokeOfTheDay joke = new JokeOfTheDay();
            modelMapper.map(getMissingJoke(), joke);
            return joke;
        });
    }

    /**
     * Return joke of the day matching specific date
     * @param date date only, no time (yyyy-MM-dd)
     * @return Joke of the Day
     */
    public JokeOfTheDay getJokeOfTheDayForDate(LocalDate date) {

        return jokeOfTheDayRepository.findForGivenDate(sdf.format(date))
                .map(jokeOfTheDayEntity -> {
                    JokeOfTheDay joke = new JokeOfTheDay();
                    modelMapper.map(jokeOfTheDayEntity, joke);
                    return joke;
                }).orElseThrow(() -> new JokeNotFoundException(String.format("Joke not found for date %s", sdf.format(date))));
    }

    /**
     * Persist new joke to the database
     * @param jokeOfTheDay joke to be persisted
     * @return Returns the given joke with the
     */
    public JokeOfTheDay createJokeOfTheDay(JokeOfTheDay jokeOfTheDay) {
        JokeOfTheDayEntity jokeOfTheDayEntity = new JokeOfTheDayEntity();

        validateJokeOfTheDay(jokeOfTheDay);

        modelMapper.map(jokeOfTheDay, jokeOfTheDayEntity);
        JokeOfTheDayEntity newJokeEntity = jokeOfTheDayRepository.save(jokeOfTheDayEntity);

        modelMapper.map(newJokeEntity, jokeOfTheDay);
        return jokeOfTheDay;
    }

    /**
     * Find joke with given id and update its values to those of the given argument
     * @param jokeOfTheDay
     * @param id
     * @return updated Joke
     */
    public JokeOfTheDay updateJokeOfTheDay(JokeOfTheDay jokeOfTheDay, Long id) {
        // If exists, update it
        return jokeOfTheDayRepository.findById(id).map(joke -> {
            joke.setJoke(jokeOfTheDay.getJoke());
            joke.setDate(jokeOfTheDay.getDate());
            joke.setDescription(jokeOfTheDay.getDescription());
            JokeOfTheDayEntity updatedJokeEntity = jokeOfTheDayRepository.save(joke);
            modelMapper.map(updatedJokeEntity, jokeOfTheDay);
            return jokeOfTheDay;
        }).orElseThrow(() -> new JokeNotFoundException(String.format("Joke not found for id %s", id)));
    }

    /**
     * Delete joke with given id
     * @param id
     */
    public void deleteJokeOfTheDay(Long id) {
        if(!jokeOfTheDayRepository.existsById(id)) {
            throw new JokeNotFoundException(String.format("Joke with id %s was not found", id));
        }
        jokeOfTheDayRepository.deleteById(id);
    }

    /**
     * These should be paginated
     * @return List of JOTD
     */
    public List<JokeOfTheDay> getAllJokesOfTheDay() {
        Iterable<JokeOfTheDayEntity> jokes = jokeOfTheDayRepository.findAll();
        List<JokeOfTheDay> response = new ArrayList<>();
        JokeOfTheDay jotd = null;

        for(JokeOfTheDayEntity ent : jokes) {
            jotd = new JokeOfTheDay();
            modelMapper.map(ent, jotd);
            response.add(jotd);
        }

        return response;
    }

    /**
     * If no joke for today, returning a dummy (could choose random joke from db or some other solution)
     * @return dummy JOTD
     */
    private JokeOfTheDay getMissingJoke() {
        JokeOfTheDay jokeOfTheDay = new JokeOfTheDay();
        jokeOfTheDay.setJoke("The joke is on you, no joke today!");
        return jokeOfTheDay;
    }

    /**
     * Ensure required values are present. (This could be done in other ways via Jackson or Hibernate validation)
     * @param jokeOfTheDay
     */
    public void validateJokeOfTheDay(JokeOfTheDay jokeOfTheDay) {
        List<String> errors = new ArrayList<>();
        if(jokeOfTheDay.getJoke() == null) {
            errors.add(NULL_JOKE_MSG);
        }
        if(jokeOfTheDay.getDate() == null) {
            errors.add(NULL_DATE_MSG);
        }

        if(!errors.isEmpty()) {
            throw new InvalidJokeException("Joke is invalid", errors);
        }
    }
}
