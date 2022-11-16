package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsDao;
import ru.yandex.practicum.filmorate.storage.UserDao;

@Service
public class UserService {
    private final UserDao storage;
    private final FriendsDao friendsStorage;

    @Autowired
    public UserService(UserDao storage, FriendsDao friendsStorage) {
        this.storage = storage;
        this.friendsStorage = friendsStorage;
    }
    
    public Collection<User> getUsers() {
        return storage.getUsers();
    }

    public User addUser(User user) throws ValidationException {
        return storage.addUser(user);
    }

    public User updateUser(User user) throws UserNotFoundException, ValidationException {
        storage.getUserById(user.getId());
        return storage.updateUser(user);
    }

    public User getUserById(long id) throws UserNotFoundException {
        return storage.getUserById(id);
    }

    public Collection<User> getUserFriends(long userId) throws UserNotFoundException {
        return friendsStorage.getUserFriends(userId);
    }

    public void addFriend(long userId, long friendId) throws UserNotFoundException {
        storage.getUserById(userId);
        storage.getUserById(friendId);

        friendsStorage.addFriend(userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) throws UserNotFoundException {
        storage.getUserById(userId);
        storage.getUserById(friendId);

        friendsStorage.deleteFriend(userId, friendId);
    }

    public Collection<User> findCommonFriends(long userId, long friendId) throws UserNotFoundException {
        return friendsStorage.findCommonFriends(userId, friendId);
    }
}
