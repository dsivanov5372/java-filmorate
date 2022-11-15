package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Component
public class UserDbStorage implements UserDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getUsers() {
        String sql = "SELECT * FROM users";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql);

        Collection<User> users = new ArrayList<>();

        while (result.next()) {
            User user = User.builder()
                            .id(result.getLong("user_id"))
                            .email(result.getString("email"))
                            .login(result.getString("login"))
                            .name(result.getString("name"))
                            .birthday(result.getDate("birthday").toLocalDate())
                            .build();
            users.add(user);
        }

        return users;
    }

    private void validate(User user) throws ValidationException {
        String email = user.getEmail();
        if (email == null || email.isBlank() || !email.contains("@")) {
            String message = "Wrong email format";
            log.error(message);
            throw new ValidationException(message);
        }

        String login = user.getLogin();
        if (login == null || login.isBlank() || login.contains(" ")) {
            String message = "Wrong login format";
            log.error(message);
            throw new ValidationException(message);
        }

        LocalDate currentDate = LocalDate.now();
        if (currentDate.isBefore(user.getBirthday())) {
            String message = "Birthday can't be in the future";
            log.error(message);
            throw new ValidationException(message);
        }
    }

    @Override
    public User addUser(User user) throws ValidationException {
        validate(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        String sql = "INSERT INTO users (email, login, name, birthday) VALUES(?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(),
                Date.valueOf(user.getBirthday()));

        String search = "SELECT * FROM users WHERE email = ? AND login = ? AND name = ? AND birthday = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(search, user.getEmail(), user.getLogin(),
                                                        user.getName(), Date.valueOf(user.getBirthday()));
        if (!result.next()) {
            throw new UserNotFoundException("User not found!");
        }

        return User.builder()
                   .id(result.getLong("user_id"))
                   .email(result.getString("email"))
                   .login(result.getString("login"))
                   .name(result.getString("name"))
                   .birthday(result.getDate("birthday").toLocalDate())
                   .build();
    }

    @Override
    public User updateUser(User user) throws ValidationException {
        validate(user);

        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(),
                Date.valueOf(user.getBirthday()), user.getId());

        return user;
    }

    @Override
    public User getUserById(Long id) throws UserNotFoundException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);

        if (!result.next()) {
            throw new UserNotFoundException("User not found!");
        }

        return User.builder()
                   .id(result.getLong("user_id"))
                   .email(result.getString("email"))
                   .login(result.getString("login"))
                   .name(result.getString("name"))
                   .birthday(result.getDate("birthday").toLocalDate())
                   .build();
    }
}
