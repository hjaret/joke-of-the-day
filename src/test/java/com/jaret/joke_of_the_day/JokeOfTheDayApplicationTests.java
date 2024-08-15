package com.jaret.joke_of_the_day;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jaret.joke_of_the_day.api.JokeOfTheDay;
import com.jaret.joke_of_the_day.controller.JokeOfTheDayController;
import com.jaret.joke_of_the_day.exception.JokeNotFoundException;
import com.jaret.joke_of_the_day.service.JokeOfTheDayService;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class JokeOfTheDayApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private JokeOfTheDayController jokeOfTheDayController;

	@MockBean
	JwtDecoder jwtDecoder;

	@MockBean
	JokeOfTheDayService jokeOfTheDayService;


	@Test
	void contextLoads() {
		assertThat(jokeOfTheDayController).isNotNull();
	}

	@Test
	void getShouldReturnJokeForTodayFromService() throws Exception {
		when(jokeOfTheDayService.getJokeOfTheDayForToday()).thenReturn(createJokeOfTheDay());

		this.mockMvc.perform(get("/jotd")).andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("Funny joke")));

		verify(jokeOfTheDayService, times(1)).getJokeOfTheDayForToday();
	}

	@Test
	void getShouldReturnJokeForDateFromService() throws Exception {
		when(jokeOfTheDayService.getJokeOfTheDayForDate(any(LocalDate.class))).thenReturn(createJokeOfTheDay());

		this.mockMvc.perform(get("/jotd?date=2024-08-14")).andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("Funny joke")));

		verify(jokeOfTheDayService, times(1)).getJokeOfTheDayForDate(any(LocalDate.class));
	}

	@Test
	void getWithBadDateFormatShouldFail() throws Exception {
		when(jokeOfTheDayService.getJokeOfTheDayForDate(any(LocalDate.class))).thenReturn(createJokeOfTheDay());

		this.mockMvc.perform(get("/jotd?date=2024-08-")).andDo(print())
				.andExpect(status().isBadRequest());

		verify(jokeOfTheDayService, times(0)).getJokeOfTheDayForDate(any(LocalDate.class));
	}

	@Test
	void postShouldCreateJoke() throws Exception {
		when(jokeOfTheDayService.createJokeOfTheDay(any(JokeOfTheDay.class))).thenReturn(createJokeOfTheDay());
		String postBody = getPostContent();

		this.mockMvc.perform(post("/jotd")
						.content(postBody)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(content().string(containsString("Funny joke")));

		verify(jokeOfTheDayService, times(1)).createJokeOfTheDay(any(JokeOfTheDay.class));
	}

	@Test
	void postFailToCreateDuplicateJoke_IntegrityConstraintViolation() throws Exception {
		when(jokeOfTheDayService.createJokeOfTheDay(any(JokeOfTheDay.class)))
				.thenAnswer(invocation -> {throw new JdbcSQLIntegrityConstraintViolationException("Duplicate joke", "", "", 1, null, "");});
		String postBody = getPostContent();

		this.mockMvc.perform(post("/jotd")
						.content(postBody)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isBadRequest());

		verify(jokeOfTheDayService, times(1)).createJokeOfTheDay(any(JokeOfTheDay.class));
	}

	@Test
	void putShouldUpdateJoke() throws Exception {
		when(jokeOfTheDayService.updateJokeOfTheDay(any(JokeOfTheDay.class), anyLong())).thenReturn(createJokeOfTheDay());
		String putBody = getPostContent();

		this.mockMvc.perform(put("/jotd/1")
						.content(putBody)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Funny joke")));

		verify(jokeOfTheDayService, times(1)).updateJokeOfTheDay(any(JokeOfTheDay.class), anyLong());
	}

	@Test
	void putFailToUpdateMissingJoke_NotFoundStatus() throws Exception {
		when(jokeOfTheDayService.updateJokeOfTheDay(any(JokeOfTheDay.class), anyLong())).thenThrow(new JokeNotFoundException(String.format("Joke not found for id %s", 1L)));
		String putBody = getPostContent();

		this.mockMvc.perform(put("/jotd/1")
						.content(putBody)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotFound());

		verify(jokeOfTheDayService, times(1)).updateJokeOfTheDay(any(JokeOfTheDay.class), anyLong());
	}

	private JokeOfTheDay createJokeOfTheDay() {
		JokeOfTheDay joke = new JokeOfTheDay();
		joke.setId(1L);
		joke.setJoke("Funny joke");
		joke.setDate(LocalDate.now());
		return joke;
	}

	private String getPostContent() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		JokeOfTheDay joke = createJokeOfTheDay();
		joke.setId(null);
		joke.setJoke("Funny joke");
		joke.setDate(LocalDate.now());
		return mapper.writeValueAsString(joke);
	}
}
