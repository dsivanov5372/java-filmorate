package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.stream.Stream;

import ru.yandex.practicum.filmorate.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;

public class UserControllerTest {
    private UserController controller;

    @BeforeEach
    public void createController() {
        controller = new UserController();
    }

    private static Stream<Arguments> validUsers() {
        return Stream.of(
          Arguments.of(User.builder()
                        .email("mail@ya.ru")
                        .name("Big Lebowski")
                        .login("Where'sTheMoney")
                        .birthday(LocalDate.of(1942, 12, 4))
                        .build(), 
                        "Big Lebowski",
                        "mail@ya.ru",
                        "Where'sTheMoney",
                        LocalDate.of(1942, 12, 4),
                        1),
          Arguments.of(User.builder()
                        .email("mail@ya.ru")
                        .name("")
                        .login("Where'sTheMoney")
                        .birthday(LocalDate.of(1942, 12, 4))
                        .build(),
                        "Where'sTheMoney",
                        "mail@ya.ru",
                        "Where'sTheMoney",
                        LocalDate.of(1942, 12, 4),
                        1));
    }
    
    private static Stream<Arguments> invalidUsers() {
        return Stream.of(
          Arguments.of(User.builder()
                        .email("something's wrong i can feel it")
                        .name("Big Lebowski")
                        .login("Where'sTheMoney")
                        .birthday(LocalDate.of(1942, 12, 4))
                        .build(),
                        "Wrong email format"),
          Arguments.of(User.builder()
                        .email("mail@ya.ru")
                        .name("Big Lebowski")
                        .login("")
                        .birthday(LocalDate.of(1942, 12, 4))
                        .build(),
                        "Wrong login format"));
    }

    @ParameterizedTest
    @MethodSource("validUsers")
    public void addUserIfValid(User user, String name, String email, String login,
                                LocalDate birthday, int id) throws ValidationException{
        controller.addUser(user);
        ArrayList<User> arr = new ArrayList<>(controller.getUsers());
        assertEquals(arr.size(), 1);
        assertEquals(arr.get(0).getName(), name);
        assertEquals(arr.get(0).getEmail(), email);
        assertEquals(arr.get(0).getLogin(), login);
        assertEquals(arr.get(0).getBirthday(), birthday);
        assertEquals(arr.get(0).getId(), id);
    }

    @ParameterizedTest
    @MethodSource("invalidUsers")
    public void throwsExceptionIfInvalidField(User user, String message) {
        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.addUser(user);
        });
        assertEquals(exception.getMessage(), message);
        assertEquals(controller.getUsers().size(), 0);
    }
}