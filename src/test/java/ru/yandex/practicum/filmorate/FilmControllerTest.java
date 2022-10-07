package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

public class FilmControllerTest {
    private FilmController controller;

    @BeforeEach
    public void createController() {
        controller = new FilmController();
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
        assertEquals(arr.get(0).getName(), "Halt and cath fire");
        assertEquals(arr.get(0).getDescription(), "serial");
        assertEquals(arr.get(0).getDuration(), 100);
        assertEquals(arr.get(0).getReleaseDate(), LocalDate.of(2014, 6, 1));
        assertEquals(arr.get(0).getId(), 1);
    }

    private static Stream<Arguments> films() {
        String description = "";
        for (int i = 0; i < 210; ++i) {
            description += "/";
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
                        .description(description)
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
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.addFilm(film);
        });  
        assertEquals(exception.getMessage(), message);
        assertEquals(controller.getFilms().size(), 0);   
    }
}