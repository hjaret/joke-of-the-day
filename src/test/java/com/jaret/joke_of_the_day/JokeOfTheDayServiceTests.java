package com.jaret.joke_of_the_day;

import com.jaret.joke_of_the_day.api.JokeOfTheDay;
import com.jaret.joke_of_the_day.entity.JokeOfTheDayEntity;
import com.jaret.joke_of_the_day.exception.InvalidJokeException;
import com.jaret.joke_of_the_day.exception.JokeNotFoundException;
import com.jaret.joke_of_the_day.repository.JokeOfTheDayRepository;
import com.jaret.joke_of_the_day.service.JokeOfTheDayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@AutoConfigureTestDatabase
class JokeOfTheDayServiceTests {

	@MockBean
	JokeOfTheDayRepository jokeOfTheDayRepository;

	@Autowired
	JokeOfTheDayService jokeOfTheDayService;

	@MockBean
	JwtDecoder jwtDecoder;

	@Test
	void getJokeOfTheDayForTodayTest() {
		Optional<JokeOfTheDayEntity> jokeOfTheDayOptional = Optional.of(createJokeOfTheDayEntity());

		when(jokeOfTheDayRepository.findForToday()).thenReturn(jokeOfTheDayOptional);

		JokeOfTheDay joke = jokeOfTheDayService.getJokeOfTheDayForToday();

        assertEquals("Funny joke", joke.getJoke());
	}

	@Test
	void getJokeOfTheDayForTodayTest_Missing() {
		Optional<JokeOfTheDayEntity> jokeOfTheDayOptional = Optional.empty();

		when(jokeOfTheDayRepository.findForToday()).thenReturn(jokeOfTheDayOptional);

		JokeOfTheDay joke = jokeOfTheDayService.getJokeOfTheDayForToday();

        assertEquals("The joke is on you, no joke today!", joke.getJoke());
	}

	@Test
	void getJokeOfTheDayForDate() {
		Optional<JokeOfTheDayEntity> jokeOfTheDayOptional = Optional.empty();
		LocalDate date = LocalDate.of(2024, 7, 20);
		when(jokeOfTheDayRepository.findForToday()).thenReturn(jokeOfTheDayOptional);

		Exception exception = assertThrows(JokeNotFoundException.class, () -> {
			jokeOfTheDayService.getJokeOfTheDayForDate(date);
		});

		String expectedErrorMsg = "Joke not found for date 2024-07-20";
		String actual = exception.toString();

		assertTrue(actual.contains(expectedErrorMsg));
	}

	@Test
	void createJokeOfTheDay() {

		JokeOfTheDay joke = getJokeOfTheDay();
		JokeOfTheDayEntity entity = createJokeOfTheDayEntity();
		entity.setId(999L);

		when(jokeOfTheDayRepository.save(any(JokeOfTheDayEntity.class))).thenReturn(entity);

		JokeOfTheDay newJoke = jokeOfTheDayService.createJokeOfTheDay(joke);

        assertEquals(999L, (long) newJoke.getId());
	}

	@Test
	void updateJokeOfTheDay_Missing() {
		Optional<JokeOfTheDayEntity> jokeOfTheDayOptional = Optional.empty();
		when(jokeOfTheDayRepository.findForToday()).thenReturn(jokeOfTheDayOptional);

		Exception exception = assertThrows(JokeNotFoundException.class, () -> {
			jokeOfTheDayService.updateJokeOfTheDay(getJokeOfTheDay(), 999L);
		});

		String expectedErrorMsg = "Joke not found for id 999";
		String actual = exception.toString();

		assertTrue(actual.contains(expectedErrorMsg));
	}

	@Test
	void deleteJokeOfTheDay_Missing() {
		Optional<JokeOfTheDayEntity> jokeOfTheDayOptional = Optional.empty();
		when(jokeOfTheDayRepository.findForToday()).thenReturn(jokeOfTheDayOptional);

		Exception exception = assertThrows(JokeNotFoundException.class, () -> {
			jokeOfTheDayService.deleteJokeOfTheDay(999L);
		});

		String expectedErrorMsg = "Joke not found for id 999";
		String actual = exception.toString();

		System.out.println(actual);

		assertTrue(actual.contains(expectedErrorMsg));
	}

	@Test
	void validateInvalidJokeOfTheDay() {

		JokeOfTheDay joke = getBadJokeOfTheDay();

		Exception exception = assertThrows(InvalidJokeException.class, () -> {
			jokeOfTheDayService.validateJokeOfTheDay(joke);
		});

		String expectedErrorMsg = "Joke field cannot be null";
		String actual = exception.toString();

		assertTrue(actual.contains(expectedErrorMsg));
	}

	private JokeOfTheDay getJokeOfTheDay() {
		JokeOfTheDay joke = new JokeOfTheDay();
		joke.setJoke("Funny joke");
		joke.setDate(LocalDate.now());
		return joke;
	}

	private JokeOfTheDay getBadJokeOfTheDay() {
		JokeOfTheDay joke = new JokeOfTheDay();
		joke.setDate(LocalDate.now());
		return joke;
	}

	private JokeOfTheDayEntity createJokeOfTheDayEntity() {
		JokeOfTheDayEntity joke = new JokeOfTheDayEntity();
		joke.setId(1L);
		joke.setJoke("Funny joke");
		joke.setDate(LocalDate.now());
		return joke;
	}


}
