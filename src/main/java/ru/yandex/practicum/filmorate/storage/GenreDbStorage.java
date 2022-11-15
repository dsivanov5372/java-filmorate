package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component
public class GenreDbStorage implements GenreDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> getGenres() {
        String sql = "SELECT genre_id, name FROM genres ORDER BY genre_id";
        Collection<Genre> genres = new ArrayList<>();
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);

        while (result.next()) {
            Genre genre = Genre.builder()
                               .name(result.getString("name"))
                               .id(result.getInt("genre_id"))
                               .build();
            genres.add(genre);
        }

        return genres;
    }

    @Override
    public Genre getGenre(int id) throws GenreNotFoundException {
        String sql = "SELECT genre_id, name FROM genres WHERE genre_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);

        if (!result.next()) {
            throw new GenreNotFoundException("Genre not found!");
        }

        return Genre.builder()
                    .name(result.getString("name"))
                    .id(result.getInt("genre_id"))
                    .build();
    }

    @Override
    public Set<Genre> getGenresOfFilm(long id) {
        String sql = "SELECT * FROM film_genres AS fg " +
                     "LEFT OUTER JOIN genres AS g ON g.genre_id = fg.genre_id " +
                     "WHERE fg.film_id = ? " +
                     "ORDER BY fg.genre_id";

        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
        Set<Genre> genres = new HashSet<>();

        while (result.next()) {
            Genre genre = Genre.builder()
                    .name(result.getString("name"))
                    .id(result.getInt("genre_id"))
                    .build();
            genres.add(genre);
        }

        return genres;
    }

    @Override
    public void addFilmGenres(long id, Set<Genre> genres) {
        if (genres != null) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES(?, ?)";

            for (Genre genre : genres) {
                jdbcTemplate.update(sql, id, genre.getId());
            }
        }
    }

    @Override
    public void updateFilmGenres(long id, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            String sql = "DELETE FROM film_genres WHERE film_id = ?";
            jdbcTemplate.update(sql, id);
        } else {
            Set<Genre> previous = getGenresOfFilm(id);
            for (Genre genre : previous) {
                if (!genres.contains(genre)) {
                    String delete = "DELETE FROM film_genres WHERE film_id = ? AND genre_id = ?";
                    jdbcTemplate.update(delete, id, genre.getId());
                }
            }

            for (Genre genre : genres) {
                if (!previous.contains(genre)) {
                    String insert = "INSERT INTO film_genres (film_id, genre_id) VALUES(?, ?)";
                    jdbcTemplate.update(insert, id, genre.getId());
                }
            }
        }
    }
}
