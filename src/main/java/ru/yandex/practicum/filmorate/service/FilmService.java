package ru.yandex.practicum.filmorate.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) throws ValidationException {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) throws ValidationException {
        return filmStorage.updateFilm(film);
    }

    public void addLike(long filmId, long userId) throws RuntimeException {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        film.addLike(user.getId());
    }

    public void removeLike(long filmId, long userId) throws RuntimeException {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        film.addLike(user.getId());
    }

    public Collection<Film> getMostPopularFilms(int count) {
        ArrayList<Film> films = new ArrayList<>(filmStorage.getFilms());
        Collections.sort(films, (lhs, rhs) -> 
                    lhs.getUsersLikes().size() < rhs.getUsersLikes().size() ?
                    1 : lhs.getUsersLikes().size() < rhs.getUsersLikes().size() ? 0 : -1);

        if (films.size() <= count) {
            return films;
        }
        return films.subList(0, count);
    }

    public Film getFilm(long id) throws RuntimeException {
        return filmStorage.getFilmById(id);
    }
}
