package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDao;
import ru.yandex.practicum.filmorate.storage.LikesDao;
import ru.yandex.practicum.filmorate.storage.UserDao;
import java.time.LocalDate;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
public class LikesDaoTest {
    private final LikesDao likesDao;
    private final FilmDao filmDao;
    private final UserDao userDao;

    @Autowired
    public LikesDaoTest(LikesDao likesDao, FilmDao filmDao, UserDao userDao) {
        this.likesDao = likesDao;
        this.filmDao = filmDao;
        this.userDao = userDao;
    }

    @Test
    public void addLikeToFilms() throws ValidationException {
        Film film1 = Film.builder()
                .name("The boys 10001")
                .description("serial")
                .duration(100)
                .releaseDate(LocalDate.of(2014, 6, 1))
                .mpa(MpaRating.builder().id(5).build())
                .build();

        Film film2 = Film.builder()
                .name("The boys 10002")
                .description("serial")
                .duration(100)
                .releaseDate(LocalDate.of(2014, 6, 1))
                .mpa(MpaRating.builder().id(5).build())
                .build();

        User user1 = User.builder()
                        .email("mail1001@yandex.ru")
                        .name("Аркадий Волож")
                        .login("login1001")
                        .birthday(LocalDate.of(1942, 12, 4))
                        .build();

        User user2 = User.builder()
                        .email("mail1002@yandex.ru")
                        .name("Илья Сегалович")
                        .login("login1002")
                        .birthday(LocalDate.of(1942, 12, 4))
                        .build();

        user1 = userDao.addUser(user1);
        user2 = userDao.addUser(user2);
        film1 = filmDao.addFilm(film1);
        film2 = filmDao.addFilm(film2);
        likesDao.addLike(film1.getId(), user1.getId());
        likesDao.addLike(film1.getId(), user2.getId());
        likesDao.addLike(film2.getId(), user1.getId());

        ArrayList<Film> films = (ArrayList<Film>) likesDao.getMostPopularFilms(2);
        assertEquals(film1, films.get(0));
        assertEquals(film2, films.get(1));

        likesDao.addLike(film2.getId(), user2.getId());
        likesDao.removeLike(film1.getId(), user2.getId());

        ArrayList<Film> films1 = (ArrayList<Film>) likesDao.getMostPopularFilms(2);
        assertEquals(film2, films1.get(0));
        assertEquals(film1, films1.get(1));
    }
}