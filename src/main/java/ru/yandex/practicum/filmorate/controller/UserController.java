package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@Slf4j
@RestController
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    private int idSetter = 1;

    @GetMapping("/users")
    public Collection<User> getUsers() {
        return users.values();
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

    private User add(User user) throws ValidationException {
        validate(user);
        users.put(user.getId(), user);
        return user;
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(idSetter++);
        log.info("/POST request, add new user {}", user);
        return add(user);   
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) throws ValidationException {
        log.info("/PUT request, add/update user {}", user);
        if (!users.containsKey(user.getId())) {
            String message = "Wrong user id";
            log.error(message);
            throw new ValidationException(message);
        }
        return add(user);
    }
}
