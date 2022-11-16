package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.Collection;
import java.util.Set;

public interface GenreDao {

    Collection<Genre> getGenres();

    Genre getGenre(int id) throws GenreNotFoundException;

    Set<Genre> getGenresOfFilm(long id);

    void addFilmGenres(long id, Set<Genre> genres);

    void updateFilmGenres(long id, Set<Genre> genres);
}
