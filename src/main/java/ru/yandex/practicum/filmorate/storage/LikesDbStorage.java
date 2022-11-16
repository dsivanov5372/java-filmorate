package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Component
public class LikesDbStorage implements LikesDao {
    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;
    private final GenreDao genreDao;

    @Autowired
    public LikesDbStorage(JdbcTemplate jdbcTemplate, MpaDao mpaDao, GenreDao genreDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.mpaDao = mpaDao;
        this.genreDao = genreDao;
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public Collection<Film> getMostPopularFilms(int count) {
        String sql = "SELECT * FROM films " +
                     "LEFT JOIN (SELECT film_id, COUNT(DISTINCT(user_id)) AS user_likes " +
                     "FROM likes GROUP BY film_id) AS p ON films.film_id = p.film_id " +
                     "ORDER BY p.user_likes DESC LIMIT ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, count);

        Collection<Film> films = new ArrayList<>();
        while (result.next()) {
            int mpaId = result.getInt("rating_id");
            MpaRating mpa = mpaDao.getMpaRating(mpaId);

            Film film = Film.builder().id(result.getLong("film_id"))
                    .name(result.getString("name"))
                    .description(result.getString("description"))
                    .releaseDate(result.getDate("release_date").toLocalDate())
                    .duration(result.getInt("duration"))
                    .mpa(mpa)
                    .build();

            Set<Genre> genres = genreDao.getGenresOfFilm(film.getId());
            film.setGenres(genres);
            films.add(film);
        }

        return films;
    }
}
