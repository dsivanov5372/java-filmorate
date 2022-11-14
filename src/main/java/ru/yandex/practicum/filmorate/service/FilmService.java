package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmDao;
import ru.yandex.practicum.filmorate.storage.LikesDao;
import ru.yandex.practicum.filmorate.storage.UserDao;

@Service
public class FilmService {
    private final FilmDao filmDao;
    private final LikesDao likesDao;
    private final UserDao userDao;

    @Autowired
    public FilmService(FilmDao filmDao, LikesDao likesDao, UserDao userDao) {
        this.filmDao = filmDao;
        this.likesDao = likesDao;
        this.userDao = userDao;
    }

    public Collection<Film> getFilms() {
        return filmDao.getFilms();
    }

    public Film addFilm(Film film) throws ValidationException {
        return filmDao.addFilm(film);
    }

    public Film updateFilm(Film film) throws ValidationException, RuntimeException {
        filmDao.getFilmById(film.getId());
        return filmDao.updateFilm(film);
    }

    public void addLike(long filmId, long userId) throws RuntimeException {
        filmDao.getFilmById(filmId);
        userDao.getUserById(userId);

        likesDao.addLike(filmId, userId);
    }

    public void removeLike(long filmId, long userId) throws RuntimeException {
        filmDao.getFilmById(filmId);
        userDao.getUserById(userId);

        likesDao.removeLike(filmId, userId);
    }

    public Collection<Film> getMostPopularFilms(int count) {
        return likesDao.getMostPopularFilms(count);
    }

    public Film getFilm(long id) throws RuntimeException {
        return filmDao.getFilmById(id);
    }
}
