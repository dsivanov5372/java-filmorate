package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.MpaRatingNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import java.util.Collection;

public interface MpaDao {

    Collection<MpaRating> getMpaRatings();

    MpaRating getMpaRating(int id) throws MpaRatingNotFoundException;
}
