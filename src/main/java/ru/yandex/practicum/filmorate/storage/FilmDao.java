package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

public interface FilmDao {
    Collection<Film> getFilms();
    
    Film addFilm(Film film) throws ValidationException;

    Film updateFilm(Film film) throws ValidationException, RuntimeException;

    Film getFilmById(long id) throws RuntimeException;
}