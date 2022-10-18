package ru.yandex.practicum.filmorate.storage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Long, User> users = new HashMap<>();
    private long idSetter = 1;

    @Override
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
        user.setId(idSetter++);
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        users.put(user.getId(), user);
        return user;
    }

    private User update(User user) throws ValidationException {
        validate(user);
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User addUser(User user) throws ValidationException {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        log.info("/POST request, add new user {}", user);
        return add(user);   
    }

    @Override
    public User updateUser(User user) throws RuntimeException, ValidationException {
        log.info("/PUT request, add/update user {}", user);
        if (!users.containsKey(user.getId())) {
            String message = "Wrong user id";
            log.error(message);
            throw new UserNotFoundException(message);
        }
        return update(user);
    }

    @Override
    public Collection<User> getUserFriends(long userId) throws RuntimeException {
        User user = users.get(userId);
        if (user == null) {
            throw new UserNotFoundException("Wrong user id");
        }

        ArrayList<User> friends = new ArrayList<>();
        user.getFriends().stream().forEach((id) -> friends.add(users.get(id)));

        return friends;
    }

    @Override
    public User getUserById(Long id) throws RuntimeException {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException("Wrong user id");
        }
        return user;
    }
}
