package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.MpaRatingNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaDao;
import java.util.Collection;

@Service
public class MpaService {
    private final MpaDao mpaDao;

    @Autowired
    public MpaService(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    public Collection<MpaRating> getMpaRatings() {
        return mpaDao.getMpaRatings();
    }

    public MpaRating getMpaRating(int id) throws MpaRatingNotFoundException {
        return mpaDao.getMpaRating(id);
    }
}
