package com.jaret.joke_of_the_day.controller;

import com.jaret.joke_of_the_day.api.JokeOfTheDay;
import com.jaret.joke_of_the_day.service.JokeOfTheDayService;
import com.jaret.joke_of_the_day.service.JokeOfTheDaySourceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path="/jotd", produces="application/json")
public class JokeOfTheDayController {

    private final JokeOfTheDaySourceInterface jokeOfTheDayService;

    @Autowired
    public JokeOfTheDayController(JokeOfTheDayService jokeOfTheDayService) {
        this.jokeOfTheDayService = jokeOfTheDayService;
    }

    /**
     * Retrieve joke by id
     * @param id
     * @return Joke retrieved by service
     */
    @GetMapping("/{id}")
    public ResponseEntity<JokeOfTheDay> updateJOTD(@PathVariable("id") Long id) {
        return ResponseEntity.ok(jokeOfTheDayService.getJokeOfTheDayById(id));
    }

    /**
     * Retrieve joke of the day. If date is omitted then today's joke will be returned, otherwise the joke matching the date will be returned
     * @param date null or "yyyy-MM-dd"
     * @return Joke retrieved by service
     */
    @GetMapping
    public ResponseEntity<JokeOfTheDay> getJOTD(@RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        JokeOfTheDay jotd = null;

        if( date != null ) {
            jotd = jokeOfTheDayService.getJokeOfTheDayForDate(date);
        } else {
            jotd = jokeOfTheDayService.getJokeOfTheDayForToday();
        }

        return ResponseEntity.ok(jotd);
    }

    /**
     * Create a new JOTD
     * @param jokeOfTheDay
     * @return newly created JOTD
     */
    @PostMapping
    public ResponseEntity<JokeOfTheDay> createJOTD(@RequestBody JokeOfTheDay jokeOfTheDay) {

        JokeOfTheDay jotd = jokeOfTheDayService.createJokeOfTheDay(jokeOfTheDay);

        return ResponseEntity.status(HttpStatus.CREATED).body(jotd);
    }

    /**
     * Create or update JOTD
     * @param newJokeOfTheDay
     * @param id
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<JokeOfTheDay> updateJOTD(@RequestBody JokeOfTheDay newJokeOfTheDay, @PathVariable("id") Long id) {

        JokeOfTheDay jotd = jokeOfTheDayService.updateJokeOfTheDay(newJokeOfTheDay, id);

        return ResponseEntity.status(HttpStatus.OK).body(jotd);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<JokeOfTheDay> deleteJOTD(@PathVariable("id") Long id) {
        jokeOfTheDayService.deleteJokeOfTheDay(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping(path="/list")
    public ResponseEntity<List<JokeOfTheDay>> getAllJOTD() {

        List<JokeOfTheDay> jotd = jokeOfTheDayService.getAllJokesOfTheDay();
        return ResponseEntity.ok(jotd);
    }
}
