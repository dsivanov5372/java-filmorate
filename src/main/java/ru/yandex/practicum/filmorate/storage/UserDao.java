package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

public interface UserDao {
    Collection<User> getUsers();

    User addUser(User user) throws ValidationException;

    User updateUser(User user) throws RuntimeException, ValidationException;

    User getUserById(Long id) throws RuntimeException;
}
