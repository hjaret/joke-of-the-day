package com.jaret.joke_of_the_day;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jaret.joke_of_the_day.api.JokeOfTheDay;
import com.jaret.joke_of_the_day.controller.JokeOfTheDayController;
import com.jaret.joke_of_the_day.entity.JokeOfTheDayEntity;
import com.jaret.joke_of_the_day.repository.JokeOfTheDayRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureTestDatabase
class JokeOfTheDayIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private JokeOfTheDayController jokeOfTheDayController;

	@Autowired
	private JokeOfTheDayRepository jokeOfTheDayRepository;

	@MockBean
	JwtDecoder jwtDecoder;

	JokeOfTheDayEntity entity;

	private static ObjectMapper mapper;

	@BeforeAll
	static void beforeAll() {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
	}

	@BeforeEach
	void beforeEach() {
		System.out.println("saving entity");
		entity = jokeOfTheDayRepository.save(createJokeOfTheDayEntity());
	}

	@AfterEach
	void afterEach() throws InterruptedException {
		System.out.println("deleting entity");
		jokeOfTheDayRepository.delete(entity);
		Thread.sleep(200);
	}

	@Test
	void contextLoads() {
		assertThat(jokeOfTheDayController).isNotNull();
	}

	@Test
	void getShouldReturnJokeForTodayFromService() throws Exception {
		this.mockMvc.perform(get("/jotd"))
				.andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString("Funny joke")));
	}

	@Test
	void getShouldReturnJokeForDateFromService() throws Exception {
		this.mockMvc.perform(get("/jotd?date=2024-08-15"))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("Funny joke")));
	}

	@Test
	void getWithBadDateFormatShouldFail() throws Exception {
		this.mockMvc.perform(get("/jotd?date=2024-08-")).andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void postShouldCreateJoke() throws Exception {
		String postBody = getPostContent();

		// delete existing record for today to avoid collision
		afterEach();

		MvcResult result = this.mockMvc.perform(post("/jotd")
						.content(postBody)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(content().string(containsString("Funny joke")))
				.andReturn();

		String json = result.getResponse().getContentAsString();
		JokeOfTheDay joke = mapper.readValue(json, JokeOfTheDay.class);

		// Delete record created by the POST
		jokeOfTheDayRepository.deleteById(joke.getId());
	}

	@Test
	void postFailToCreateDuplicateJoke_IntegrityConstraintViolation() throws Exception {
		String postBody = getPostContent();

		this.mockMvc.perform(post("/jotd")
						.content(postBody)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void putShouldUpdateJoke() throws Exception {
		String putBody = getPostContent();

		this.mockMvc.perform(put("/jotd/" + entity.getId())
						.content(putBody)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString("Funny joke")));
	}

	@Test
	void putFailToUpdateMissingJoke_NotFoundStatus() throws Exception {
		String putBody = getPostContent();

		this.mockMvc.perform(put("/jotd/9999")
						.content(putBody)
						.accept(MediaType.APPLICATION_JSON)
						.contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isNotFound());
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

	private JokeOfTheDayEntity createJokeOfTheDayEntity() {
		JokeOfTheDayEntity joke = new JokeOfTheDayEntity();
		joke.setJoke("Funny joke");
		joke.setDate(LocalDate.now());
		return joke;
	}

}
