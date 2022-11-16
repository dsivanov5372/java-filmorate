package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.MpaRatingNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaDao;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class MpaDaoTest {
    private final MpaDao mpaDao;

    @Autowired
    public MpaDaoTest(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    @Test
    public void returnListOfAllRatings() {
        MpaRating rating1 = MpaRating.builder().id(1).name("G").build();
        MpaRating rating2 = MpaRating.builder().id(2).name("PG").build();
        MpaRating rating3 = MpaRating.builder().id(3).name("PG-13").build();
        MpaRating rating4 = MpaRating.builder().id(4).name("R").build();
        MpaRating rating5 = MpaRating.builder().id(5).name("NC-17").build();

        Collection<MpaRating> ratings = mpaDao.getMpaRatings();

        assertTrue(ratings.contains(rating1));
        assertTrue(ratings.contains(rating2));
        assertTrue(ratings.contains(rating3));
        assertTrue(ratings.contains(rating4));
        assertTrue(ratings.contains(rating5));
        assertEquals(5, ratings.size());
    }

    @Test
    public void returnRatingIfValidId() {
        MpaRating rating = MpaRating.builder().id(1).name("G").build();
        MpaRating result = mpaDao.getMpaRating(1);

        assertEquals(rating, result);
    }

    @Test
    public void throwExceptionIfInvalidId() {
        RuntimeException exception = assertThrows(MpaRatingNotFoundException.class, () -> mpaDao.getMpaRating(10));
        assertEquals("Mpa rating not found!", exception.getMessage());
    }
}
