package ru.yandex.practicum.filmorate.service;

import java.util.ArrayList;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }
    
    public Collection<User> getUsers() {
        return storage.getUsers();
    }

    public User addUser(User user) throws ValidationException {
        return storage.addUser(user);
    }

    public User updateUser(User user) throws RuntimeException, 
                                             ValidationException {
        return storage.updateUser(user);
    }

    public User getUserById(long id) throws RuntimeException {
        return storage.getUserById(id);
    }

    public Collection<User> getUserFriends(long userId) throws RuntimeException {
        return storage.getUserFriends(userId);
    }

    public void addFriend(long userId, long friendId) throws RuntimeException {
        User user = storage.getUserById(userId);
        User friend = storage.getUserById(friendId);

        friend.addFriend(userId);
        user.addFriend(friendId);
    }

    public void deleteFriend(long userId, long friendId) throws ValidationException {
        User user = storage.getUserById(userId);
        User friend = storage.getUserById(friendId);

        friend.deleteFriend(userId);
        user.deleteFriend(friendId);
    }

    public Collection<User> findCommonFriends(long userId, long friendId) throws RuntimeException {
        User user = storage.getUserById(userId);
        User friend = storage.getUserById(friendId);
        ArrayList<User> result = new ArrayList<>();

        ArrayList<User> userFriends = new ArrayList<>();
        user.getFriends().forEach(id -> userFriends.add(storage.getUserById(id)));

        friend.getFriends().forEach(id -> {
            User commonFriend = storage.getUserById(id);
            if (userFriends.contains(commonFriend)) {
                result.add(commonFriend);
            }
        });

        return result;
    }
}
