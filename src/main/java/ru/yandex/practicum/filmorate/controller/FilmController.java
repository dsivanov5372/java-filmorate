package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import java.time.LocalDate;

@Slf4j
@RestController
public class FilmController {
    private Map<Integer, Film> films = new HashMap<>();
    private int idSetter = 1;

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        return films.values();
    }

    private void validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            String message = "Name of the film can't be empty";
            log.error(message);
            throw new ValidationException(message);
        }

        if (film.getDescription().length() > 200) {
            String message = "The length of description can't be more than 200 characters";
            log.error(message);
            throw new ValidationException(message);
        }

        LocalDate border = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(border)) {
            String message = "The release date of the film can't be before than 28/12/1985";
            log.error(message);
            throw new ValidationException(message);
        }

        if (film.getDuration() <= 0) {
            String message = "The duration can't be negative or zero";
            log.error(message);
            throw new ValidationException(message);
        }
    }

    private Film add(Film film) throws ValidationException {
        validate(film);
        films.put(film.getId(), film);
        return film;
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        film.setId(idSetter++);
        log.info("/POST request, add new film {}", film);
        return add(film);
    }

    @PutMapping("/films") 
    public Film updateFilm(@Valid @RequestBody Film film) throws ValidationException {
        log.info("/PUT request, add/update film {}", film);
        if (!films.containsKey(film.getId())) {
            String message = "Wrong fild id";
            log.error(message);
            throw new ValidationException(message);
        }
        return add(film);
    }
}