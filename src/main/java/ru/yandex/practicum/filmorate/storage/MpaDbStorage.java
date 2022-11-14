package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.MpaRatingNotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class MpaDbStorage implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<MpaRating> getMpaRatings() {
        String sql = "SELECT mpa_rating_id, name FROM mpa_ratings ORDER BY mpa_rating_id";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
        Collection<MpaRating> ratings = new ArrayList<>();

        while (result.next()) {
            MpaRating rating = MpaRating.builder()
                                        .name(result.getString("name"))
                                        .id(result.getInt("mpa_rating_id"))
                                        .build();
            ratings.add(rating);
        }

        return ratings;
    }

    @Override
    public MpaRating getMpaRating(int id) throws RuntimeException {
        String sql = "SELECT mpa_rating_id, name FROM mpa_ratings WHERE mpa_rating_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);

        if (!result.next()) {
            throw new MpaRatingNotFoundException("Mpa rating not found!");
        }

        return MpaRating.builder()
                        .name(result.getString("name"))
                        .id(result.getInt("mpa_rating_id"))
                        .build();
    }
}
