package com.jaret.joke_of_the_day;

import com.jaret.joke_of_the_day.api.JokeOfTheDay;
import com.jaret.joke_of_the_day.exception.InvalidJokeException;
import com.jaret.joke_of_the_day.repository.JokeOfTheDayRepository;
import com.jaret.joke_of_the_day.service.JokeOfTheDayService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
class JokeOfTheDayServiceTests {

//	@Autowired
//	private MockMvc mockMvc;
//
//	@Autowired
//	private WebApplicationContext context;

//	@Autowired
//	private JokeOfTheDayController jokeOfTheDayController;

	@MockBean
	JokeOfTheDayRepository jokeOfTheDayRepository;

	@Autowired
	JokeOfTheDayService jokeOfTheDayService;

	@Test
	void getShouldReturnJokeForTodayFromService() throws Exception {

		JokeOfTheDay joke = getBadJokeOfTheDay();

		Exception exception = assertThrows(InvalidJokeException.class, () -> {
			jokeOfTheDayService.validateJokeOfTheDay(joke);
		});

		String expectedErrorMsg = "Joke field cannot be null";
		String actual = exception.toString();

		assertTrue(actual.contains(expectedErrorMsg));

//		when(jokeOfTheDayRepository.getJokeOfTheDayForToday()).thenReturn(createJokeOfTheDay());
//
//		this.mockMvc.perform(get("/jotd")).andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("Funny joke")));
//
//		verify(jokeOfTheDayService, times(1)).getJokeOfTheDayForToday();
	}


	private JokeOfTheDay getBadJokeOfTheDay() {
		JokeOfTheDay joke = new JokeOfTheDay();
		joke.setDate(LocalDate.now());
		return joke;
	}

}
