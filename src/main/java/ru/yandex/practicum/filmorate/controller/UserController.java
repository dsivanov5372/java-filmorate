package ru.yandex.practicum.filmorate.controller;

import java.util.Collection;
import javax.validation.Valid;

import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class UserController {
    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }
    
    @GetMapping("/users")
    public Collection<User> getUsers() {
        return service.getUsers();
    }

    @PostMapping("/users")
    public User addUser(@Valid @RequestBody User user) throws ValidationException {
        return service.addUser(user);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) 
                throws UserNotFoundException, ValidationException {
        return service.updateUser(user);
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") String userId,
                          @PathVariable("friendId") String friendId)
                          throws UserNotFoundException {
        service.addFriend(Long.parseLong(userId), Long.parseLong(friendId));
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") String userId,
                             @PathVariable("friendId") String friendId) {
        service.deleteFriend(Long.parseLong(userId), Long.parseLong(friendId));
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable("id") String userId,
                                              @PathVariable("otherId") String friendId)
                                              throws RuntimeException {
        return service.findCommonFriends(Long.parseLong(userId), Long.parseLong(friendId));
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable("id") String userId) throws UserNotFoundException {
        return service.getUserById(Long.parseLong(userId));
    }

    @GetMapping("/users/{id}/friends")
    public Collection<User> getUserFrinds(@PathVariable("id") String userId) throws UserNotFoundException {
        return service.getUserFriends(Long.parseLong(userId));
    }
}
