package ru.yandex.practicum.filmorate.storage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage{
    private Map<Long, Film> films = new HashMap<>();
    private int idSetter = 1;

    @Override
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
        film.setId(idSetter++);
        if (film.getUsersLikes() == null) {
            film.setUsersLikes(new HashSet<>());
        }
        films.put(film.getId(), film);
        return film;
    }

    private Film update(Film film) throws ValidationException {
        validate(film);
        if (film.getUsersLikes() == null) {
            film.setUsersLikes(new HashSet<>());
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
        log.info("/POST request, add new film {}", film);
        return add(film);
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException, RuntimeException {
        log.info("/PUT request, add/update film {}", film);
        if (!films.containsKey(film.getId())) {
            String message = "Film with this id doesn't exist";
            log.error(message);
            throw new UserNotFoundException(message);
        }
        return update(film);
    }

    @Override
    public Film getFilmById(long id) throws RuntimeException{
        Film film = films.get(id);
        if (film == null) {
            throw new FilmNotFoundException("Wrong film id");
        }
        return film;
    }
}
