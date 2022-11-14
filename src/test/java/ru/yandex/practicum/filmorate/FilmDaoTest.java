package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmDao;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class FilmDaoTest {
    private final FilmDao filmDao;

    @Autowired
    public FilmDaoTest(FilmDao filmDao) {
        this.filmDao = filmDao;
    }

    @Test
    public void addFilmIfValid() throws ValidationException {
        Set<Genre> genres = new HashSet<>();
        genres.add(Genre.builder().id(4).build());

        Film film = Film.builder()
                        .name("The boys 100")
                        .description("serial")
                        .duration(100)
                        .releaseDate(LocalDate.of(2014, 6, 1))
                        .mpa(MpaRating.builder().id(5).build())
                        .genres(genres)
                        .build();

        film = filmDao.addFilm(film);
        assertEquals("NC-17", film.getMpa().getName());
        assertEquals("Триллер", new ArrayList<>(film.getGenres()).get(0).getName());
        assertEquals(film, filmDao.getFilmById(film.getId()));
    }

    private static Stream<Arguments> films() {
        String description = "/".repeat(210);

        return Stream.of(
                Arguments.of(Film.builder()
                                .name("")
                                .description("serial")
                                .duration(100)
                                .releaseDate(LocalDate.of(2014, 6, 1))
                                .mpa(MpaRating.builder().id(5).build())
                                .build(),
                        "Name of the film can't be empty"),
                Arguments.of(Film.builder()
                                .name("Halt and catch fire")
                                .description(description)
                                .duration(100)
                                .releaseDate(LocalDate.of(2014, 6, 1))
                                .mpa(MpaRating.builder().id(5).build())
                                .build(),
                        "The length of description can't be more than 200 characters"),
                Arguments.of(Film.builder()
                                .name("Halt and cath fire")
                                .description("serial")
                                .duration(100)
                                .releaseDate(LocalDate.of(1600, 6, 1))
                                .mpa(MpaRating.builder().id(5).build())
                                .build(),
                        "The release date of the film can't be before than 28/12/1985"),
                Arguments.of(Film.builder()
                                .name("Halt and cath fire")
                                .description("serial")
                                .duration(-200)
                                .releaseDate(LocalDate.of(2014, 6, 1))
                                .mpa(MpaRating.builder().id(5).build())
                                .build(),
                        "The duration can't be negative or zero"));
    }

    @ParameterizedTest
    @MethodSource("films")
    public void throwsExceptionIfInvalidField(Film film, String message) {
        ValidationException exception = assertThrows(ValidationException.class, () -> filmDao.addFilm(film));
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void returnListOfFilmsIfNotEmpty() throws ValidationException {
        Film film1 = Film.builder()
                        .name("Halt and catch fire 100")
                        .description("serial")
                        .duration(100)
                        .releaseDate(LocalDate.of(2014, 6, 1))
                        .mpa(MpaRating.builder().id(5).build())
                        .build();

        Film film2 = Film.builder()
                        .name("Halt and catch fire 101")
                        .description("serial")
                        .duration(100)
                        .releaseDate(LocalDate.of(2014, 6, 1))
                        .mpa(MpaRating.builder().id(5).build())
                        .build();

        filmDao.addFilm(film1);
        filmDao.addFilm(film2);
        Collection<Film> films = filmDao.getFilms();

        assertTrue(films.contains(film1));
        assertTrue(films.contains(film2));
    }

    @Test
    public void updateFilmIfValid() throws ValidationException {
        Film film = Film.builder()
                .name("Halt and catch fire 103")
                .description("serial")
                .duration(100)
                .releaseDate(LocalDate.of(2014, 6, 1))
                .mpa(MpaRating.builder().id(5).build())
                .build();

        filmDao.addFilm(film);

        Set<Genre> genres = new HashSet<>();
        genres.add(Genre.builder().id(4).build());
        Film newFilm = Film.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .duration(film.getDuration())
                .releaseDate(film.getReleaseDate())
                .mpa(film.getMpa())
                .genres(genres)
                .build();
        filmDao.updateFilm(newFilm);

        assertEquals(newFilm, filmDao.getFilmById(newFilm.getId()));
    }

    @ParameterizedTest
    @MethodSource("films")
    public void throwExceptionWhenUpdateAndFieldsNotValid(Film film, String message) throws ValidationException {
        Film film1 = Film.builder()
                .name("Halt and catch fire 104")
                .description("serial")
                .duration(100)
                .releaseDate(LocalDate.of(2014, 6, 1))
                .mpa(MpaRating.builder().id(5).build())
                .build();

        filmDao.addFilm(film1);
        film.setId(film1.getId());

        ValidationException exception = assertThrows(ValidationException.class, () -> filmDao.updateFilm(film));
        assertEquals(message, exception.getMessage());
        assertEquals(film1, filmDao.getFilmById(film.getId()));
    }

    @Test
    public void throwExceptionIfFilmDoesNotExist() {
        RuntimeException exception = assertThrows(FilmNotFoundException.class, () -> filmDao.getFilmById(11111));
        assertEquals("Film not found!", exception.getMessage());
    }
}