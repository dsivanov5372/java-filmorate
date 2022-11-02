package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

public class FilmControllerTest {
    private FilmController controller;
    private InMemoryUserStorage storage;

    @BeforeEach
    public void createController() {
        storage = new InMemoryUserStorage();
        controller = new FilmController(new FilmService(
                                            new InMemoryFilmStorage(),
                                            storage
                                        ));
    }

    @Test
    public void addFilmIfValidFields() throws ValidationException{
        Film film = Film.builder()
                    .name("Halt and cath fire")
                    .description("serial")
                    .duration(100)
                    .releaseDate(LocalDate.of(2014, 6, 1))
                    .build();
        controller.addFilm(film);

        ArrayList<Film> arr = new ArrayList<>(controller.getFilms());
        assertEquals("Halt and cath fire", arr.get(0).getName());
        assertEquals("serial", arr.get(0).getDescription());
        assertEquals(100, arr.get(0).getDuration());
        assertEquals(LocalDate.of(2014, 6, 1), arr.get(0).getReleaseDate());
        assertEquals(1, arr.get(0).getId());
    }

    private static Stream<Arguments> films() {
        StringBuilder description = new StringBuilder();
        for (int i = 0; i < 210; ++i) {
            description.append('/');
        }

        return Stream.of(
          Arguments.of(Film.builder()
                        .name("")
                        .description("serial")
                        .duration(100)
                        .releaseDate(LocalDate.of(2014, 6, 1))
                        .build(), 
                        "Name of the film can't be empty"),
          Arguments.of(Film.builder()
                        .name("Halt and catch fire")
                        .description(description.toString())
                        .duration(100)
                        .releaseDate(LocalDate.of(2014, 6, 1))
                        .build(),
                        "The length of description can't be more than 200 characters"),
          Arguments.of(Film.builder()
                        .name("Halt and cath fire")
                        .description("serial")
                        .duration(100)
                        .releaseDate(LocalDate.of(1600, 6, 1))
                        .build(),
                        "The release date of the film can't be before than 28/12/1985"),
          Arguments.of(Film.builder()
                        .name("Halt and cath fire")
                        .description("serial")
                        .duration(-200)
                        .releaseDate(LocalDate.of(2014, 6, 1))
                        .build(),
                        "The duration can't be negative or zero"));
    }

    @ParameterizedTest
    @MethodSource("films")
    public void throwsExceptionIfInvalidField(Film film, String message) {
        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(film));  
        assertEquals(exception.getMessage(), message);
        assertEquals(controller.getFilms().size(), 0);   
    }

    private static Stream<Arguments> usersAndFilmsForLikes() {
        Film film = Film.builder()
                    .name("Halt and catch fire")
                    .description("serial")
                    .duration(100)
                    .releaseDate(LocalDate.of(2014, 6, 1))
                    .build();
        User user = User.builder()
                    .email("mail@ya.ru")
                    .name("Big Lebowski")
                    .login("Where'sTheMoney")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();
        return Stream.of(Arguments.of(film, user, 1, 2, "Wrong user id"),
                         Arguments.of(film, user, 2, 1, "Wrong film id"));
    }

    @Test
    public void addLikeIfHasUserAndFilm() throws ValidationException, RuntimeException {
        Film film = Film.builder()
        .name("Halt and catch fire")
        .description("serial")
        .duration(100)
        .releaseDate(LocalDate.of(2014, 6, 1))
        .build();
        User user = User.builder()
        .email("mail@ya.ru")
        .name("Big Lebowski")
        .login("Where'sTheMoney")
        .birthday(LocalDate.of(1942, 12, 4))
        .build();

        controller.addFilm(film);
        storage.addUser(user);
        controller.addLike(String.valueOf(film.getId()), String.valueOf(user.getId()));
        
        assertTrue(film.getUsersLikes().contains(user.getId()));
        assertEquals(film.getUsersLikes().size(), 1);
        ArrayList<Film> films = new ArrayList<>(controller.getMostPopularFilms(String.valueOf(1)));
        assertEquals(films.get(0), film);
    }

    @ParameterizedTest
    @MethodSource("usersAndFilmsForLikes")
    public void throwsExceptionIfDoesNotHaveUserOrFilmWhenAddLike(Film film, User user, 
                long filmId, long userId, String message) throws ValidationException {
        controller.addFilm(film);
        storage.addUser(user);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            controller.addLike(String.valueOf(filmId), String.valueOf(userId));
        });

        assertEquals(exception.getMessage(), message);
        assertEquals(film.getUsersLikes().size(), 0);
    }

    @ParameterizedTest
    @MethodSource("usersAndFilmsForLikes")
    public void throwsExceptionIfDoesNotHaveUserOrFilmWhenRemoveLike(Film film, User user, 
                long filmId, long userId, String message) throws ValidationException, RuntimeException {
        controller.addFilm(film);
        storage.addUser(user);
        controller.addLike(String.valueOf(film.getId()), String.valueOf(user.getId()));
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            controller.removeLike(String.valueOf(filmId), String.valueOf(userId));
        });

        assertEquals(exception.getMessage(), message);
        assertEquals(film.getUsersLikes().size(), 1);
    }

    private static Stream<Arguments> mostPopularFilms() {
        ArrayList<Long> users = new ArrayList<>();
        users.add(1L);

        Film film1 = Film.builder()
                    .name("Halt and catch fire")
                    .description("serial")
                    .duration(100)
                    .releaseDate(LocalDate.of(2014, 6, 1))
                    .usersLikes(new HashSet<>(users))
                    .build();

        users.add(2L);

        Film film2 = Film.builder()
                    .name("Halt and catch fire 2")
                    .description("serial")
                    .duration(100)
                    .releaseDate(LocalDate.of(2014, 6, 1))
                    .usersLikes(new HashSet<>(users))
                    .build();
        
        ArrayList<Film> films = new ArrayList<>(List.of(film2, film1));

        return Stream.of(Arguments.of(new ArrayList<Film>(), 0),
                         Arguments.of(films, 2));
    }

    @ParameterizedTest
    @MethodSource("mostPopularFilms")
    public void checkOrderOfFilmsWhenGetMostPopular(ArrayList<Film> films, int size) 
                throws ValidationException {
                    
        for (Film film : films) {
            try {
                controller.addFilm(film);
            } catch (ValidationException e) {
                e.printStackTrace();
            }
        }

        ArrayList<Film> popularFilms = new ArrayList<>(controller.getMostPopularFilms(String.valueOf(size)));
        assertEquals(popularFilms.size(), size);
        assertEquals(popularFilms, films);
    }

    @Test
    public void getUserIfHasIt() throws ValidationException {
        Film film = Film.builder()
                    .name("Halt and cath fire")
                    .description("serial")
                    .duration(100)
                    .releaseDate(LocalDate.of(2014, 6, 1))
                    .build();
        controller.addFilm(film);
        
        assertEquals(film, controller.getFilm("1"));
    }

    @Test
    public void throwsExceptionIfDoesNotHaveFilm() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            controller.getFilm("1");
        });

        assertEquals(exception.getMessage(), "Wrong film id");
    }
}