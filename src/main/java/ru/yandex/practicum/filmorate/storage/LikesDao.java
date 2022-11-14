package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;

public interface LikesDao {
    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    Collection<Film> getMostPopularFilms(int count);
}
