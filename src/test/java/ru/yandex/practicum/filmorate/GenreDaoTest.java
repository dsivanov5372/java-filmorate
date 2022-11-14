package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.FilmDao;
import ru.yandex.practicum.filmorate.storage.GenreDao;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class GenreDaoTest {
    private final GenreDao genreDao;
    private final FilmDao filmDao;

    @Autowired
    public GenreDaoTest(GenreDao genreDao, FilmDao filmDao) {
        this.genreDao = genreDao;
        this.filmDao = filmDao;
    }

    @Test
    public void returnListOfGenres() {
        Genre genre1 = Genre.builder().id(1).name("Комедия").build();
        Genre genre2 = Genre.builder().id(2).name("Драма").build();
        Genre genre3 = Genre.builder().id(3).name("Мультфильм").build();
        Genre genre4 = Genre.builder().id(4).name("Триллер").build();
        Genre genre5 = Genre.builder().id(5).name("Документальный").build();
        Genre genre6 = Genre.builder().id(6).name("Боевик").build();

        Collection<Genre> genres = genreDao.getGenres();

        assertTrue(genres.contains(genre1));
        assertTrue(genres.contains(genre2));
        assertTrue(genres.contains(genre3));
        assertTrue(genres.contains(genre4));
        assertTrue(genres.contains(genre5));
        assertTrue(genres.contains(genre6));
        assertEquals(6, genres.size());
    }

    @Test
    public void returnGenreIfValidId() throws RuntimeException {
        Genre genre = Genre.builder().id(1).name("Комедия").build();
        Genre result = genreDao.getGenre(1);

        assertEquals(genre, result);
    }

    @Test
    public void throwExceptionIfInvalidId() {
        RuntimeException exception = assertThrows(GenreNotFoundException.class, () -> genreDao.getGenre(10));
        assertEquals("Genre not found!", exception.getMessage());
    }

    @Test
    public void returnListOfGenresOfFilmNotEmpty() throws ValidationException {
        Genre genre1 = Genre.builder().id(1).name("Комедия").build();
        Genre genre2 = Genre.builder().id(2).name("Драма").build();
        Set<Genre> genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);

        Film film = Film.builder()
                        .name("The boys 1001")
                        .description("serial")
                        .duration(100)
                        .releaseDate(LocalDate.of(2014, 6, 1))
                        .mpa(MpaRating.builder().id(5).build())
                        .genres(genres)
                        .build();

        film = filmDao.addFilm(film);
        Set<Genre> filmGenres = genreDao.getGenresOfFilm(film.getId());

        assertTrue(filmGenres.contains(genre1));
        assertTrue(filmGenres.contains(genre2));
        assertEquals(2, filmGenres.size());
    }

    @Test
    public void returnListOfGenresOfFilmEmpty() throws ValidationException {
        Film film = Film.builder()
                        .name("The boys 1002")
                        .description("not serial")
                        .duration(100)
                        .releaseDate(LocalDate.of(2014, 6, 1))
                        .mpa(MpaRating.builder().id(5).build())
                        .build();

        film = filmDao.addFilm(film);
        Set<Genre> filmGenres = genreDao.getGenresOfFilm(film.getId());

        assertTrue(filmGenres.isEmpty());
    }

    @Test
    public void returnEmptyListIfFilmDoesNotExist() {
        Set<Genre> filmGenres = genreDao.getGenresOfFilm(1000);
        assertTrue(filmGenres.isEmpty());
    }

    @Test
    public void updateEmptyListOfGenres() throws ValidationException {
        Genre genre1 = Genre.builder().id(1).name("Комедия").build();
        Genre genre2 = Genre.builder().id(2).name("Драма").build();
        Set<Genre> genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);

        Film film = Film.builder()
                        .name("The boys 1005")
                        .description("not serial")
                        .duration(100)
                        .releaseDate(LocalDate.of(2014, 6, 1))
                        .mpa(MpaRating.builder().id(5).build())
                        .build();

        film = filmDao.addFilm(film);
        genreDao.updateFilmGenres(film.getId(), genres);
        film = filmDao.getFilmById(film.getId());

        assertIterableEquals(film.getGenres(), genres);
    }

    @Test
    public void updateListOfGenres() throws ValidationException {
        Genre genre1 = Genre.builder().id(1).name("Комедия").build();
        Genre genre2 = Genre.builder().id(2).name("Драма").build();
        Set<Genre> genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);

        Film film = Film.builder()
                .name("The boys 1006")
                .description("not serial")
                .duration(100)
                .releaseDate(LocalDate.of(2014, 6, 1))
                .mpa(MpaRating.builder().id(5).build())
                .genres(genres)
                .build();

        film = filmDao.addFilm(film);

        Set<Genre> newGenres = new HashSet<>();
        Genre genre3 = Genre.builder().id(3).name("Мультфильм").build();
        newGenres.add(genre1);
        newGenres.add(genre3);

        genreDao.updateFilmGenres(film.getId(), newGenres);
        film = filmDao.getFilmById(film.getId());

        assertIterableEquals(film.getGenres(), newGenres);
    }
}