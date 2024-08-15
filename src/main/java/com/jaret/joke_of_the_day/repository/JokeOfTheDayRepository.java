package com.jaret.joke_of_the_day.repository;

import com.jaret.joke_of_the_day.entity.JokeOfTheDayEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JokeOfTheDayRepository extends CrudRepository<JokeOfTheDayEntity, Long> {

    @Query(nativeQuery = true, value = "SELECT * from joke_of_the_day where TO_CHAR(date, 'YYYY-MM-DD')=TO_CHAR(CURRENT_DATE, 'YYYY-MM-DD')")
    Optional<JokeOfTheDayEntity> findForToday();

    @Query(nativeQuery = true, value = "SELECT * from joke_of_the_day where TO_CHAR(date, 'YYYY-MM-DD')=?1")
    Optional<JokeOfTheDayEntity> findForGivenDate(String date);

}
