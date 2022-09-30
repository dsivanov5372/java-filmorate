package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @Test
    public void throwsExceptionIfEmptyName() {
        Film film = Film.builder()
                    .name("")
                    .description("serial")
                    .duration(100)
                    .releaseDate(LocalDate.of(2014, 6, 1))
                    .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.addFilm(film);
        });  
        assertEquals(exception.getMessage(), "Name of the film can't be empty");
        assertEquals(controller.getFilms().size(), 0);
    }

    @Test
    public void throwsExceptionIfDescriptionLengthIsMoreThan200Symbols() {
        String description = "";
        for (int i = 0; i < 210; ++i) {
            description += "/";
        }

        Film film = Film.builder()
                    .name("Halt and catch fire")
                    .description(description)
                    .duration(100)
                    .releaseDate(LocalDate.of(2014, 6, 1))
                    .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.addFilm(film);
        });  
        assertEquals(exception.getMessage(), "The length of description can't be more than 200 characters");
        assertEquals(controller.getFilms().size(), 0);
    }

    @Test
    public void throwsExceptionIfReleaseDateIsBefore28December1895() {
        Film film = Film.builder()
                    .name("Halt and cath fire")
                    .description("serial")
                    .duration(100)
                    .releaseDate(LocalDate.of(1600, 6, 1))
                    .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.addFilm(film);
        });        
        assertEquals(exception.getMessage(), "The release date of the film can't be before than 28/12/1985");
        assertEquals(controller.getFilms().size(), 0);
    }

    @Test
    public void throwsExceptionIfDurationIsNegative() {
        Film film = Film.builder()
                    .name("Halt and cath fire")
                    .description("serial")
                    .duration(-200)
                    .releaseDate(LocalDate.of(2014, 6, 1))
                    .build();
        
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.addFilm(film);
        });  
        assertEquals(exception.getMessage(), "The duration can't be negative or zero");
        assertEquals(controller.getFilms().size(), 0);              
    }
}
