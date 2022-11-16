package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Slf4j
@Component
public class FilmDbStorage implements FilmDao {
    private final JdbcTemplate jdbcTemplate;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreDao genreDao, MpaDao mpaDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
    }

    @Override
    public Collection<Film> getFilms() {
        String sql = "SELECT * FROM films";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);

        Collection<Film> films = new ArrayList<>();
        while (result.next()) {
            long filmId = result.getLong("film_id");
            Set<Genre> genres = genreDao.getGenresOfFilm(filmId);
            MpaRating rating = mpaDao.getMpaRating(result.getInt("rating_id"));

            Film film = Film.builder()
                            .id(filmId)
                            .name(result.getString("name"))
                            .description(result.getString("description"))
                            .releaseDate(result.getDate("release_date").toLocalDate())
                            .duration(result.getInt("duration"))
                            .mpa(rating)
                            .genres(genres)
                            .build();
            films.add(film);
        }

        return films;
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

    @Override
    public Film addFilm(Film film) throws ValidationException {
        validate(film);

        String sql = "INSERT INTO films(name, description, release_date, duration, rating_id) VALUES(?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()),
                            film.getDuration(), film.getMpa().getId());

        String getFilm = "SELECT * FROM films WHERE name = ? AND description = ? " +
                         "AND release_date = ? AND duration = ? AND rating_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(getFilm, film.getName(), film.getDescription(),
                                Date.valueOf(film.getReleaseDate()), film.getDuration(), film.getMpa().getId());
        if (!result.next()) {
            throw new FilmNotFoundException("Film not found!");
        }

        long filmId = result.getLong("film_id");
        genreDao.addFilmGenres(filmId, film.getGenres());
        MpaRating rating = mpaDao.getMpaRating(result.getInt("rating_id"));
        Set<Genre> genres = genreDao.getGenresOfFilm(filmId);

        film.setId(filmId);
        film.setGenres(genres);
        film.setMpa(rating);
        return film;
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {
        validate(film);

        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?," +
                     " rating_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()),
                            film.getDuration(), film.getMpa().getId(), film.getId());
        genreDao.updateFilmGenres(film.getId(), film.getGenres());

        MpaRating rating = mpaDao.getMpaRating(film.getMpa().getId());
        Set<Genre> genres = genreDao.getGenresOfFilm(film.getId());
        film.setMpa(rating);
        film.setGenres(genres);
        return film;
    }

    @Override
    public Film getFilmById(long id) throws FilmNotFoundException {
        String sql = "SELECT * FROM films WHERE film_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);

        if (!result.next()) {
            throw new FilmNotFoundException("Film not found!");
        }

        MpaRating rating = mpaDao.getMpaRating(result.getInt("rating_id"));
        Set<Genre> genres = genreDao.getGenresOfFilm(id);

        return Film.builder()
                    .id(result.getLong("film_id"))
                    .name(result.getString("name"))
                    .description(result.getString("description"))
                    .releaseDate(result.getDate("release_date").toLocalDate())
                    .duration(result.getInt("duration"))
                    .mpa(rating)
                    .genres(genres)
                    .build();
    }
}
