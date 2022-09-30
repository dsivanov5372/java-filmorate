package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.ArrayList;
import ru.yandex.practicum.filmorate.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;

public class UserControllerTest {
    private UserController controller;

    @BeforeEach
    public void createController() {
        controller = new UserController();
    }

    @Test
    public void addUserIfValid() throws ValidationException {
        User user = User.builder()
                    .email("mail@ya.ru")
                    .name("Big Lebowski")
                    .login("Where'sTheMoney")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();
        
        controller.addUser(user);

        ArrayList<User> arr = new ArrayList<>(controller.getUsers());
        assertEquals(arr.get(0).getName(), "Big Lebowski");
        assertEquals(arr.get(0).getEmail(), "mail@ya.ru");
        assertEquals(arr.get(0).getLogin(), "Where'sTheMoney");
        assertEquals(arr.get(0).getBirthday(), LocalDate.of(1942, 12, 4));
        assertEquals(arr.get(0).getId(), 1);
    }

    @Test
    public void throwsExceptionIfWrongEmailAddress() {
        User user = User.builder()
                    .email("something's wrong i can feel it")
                    .name("Big Lebowski")
                    .login("Where'sTheMoney")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.addUser(user);
        }); 
        assertEquals(exception.getMessage(), "Wrong email format");
        assertEquals(controller.getUsers().size(), 0);
    }

    @Test
    public void throwsExceptionIfEmptyName() {
        User user = User.builder()
                    .email("mail@ya.ru")
                    .name("Big Lebowski")
                    .login("")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            controller.addUser(user);
        }); 
        assertEquals(exception.getMessage(), "Wrong login format");
        assertEquals(controller.getUsers().size(), 0);
    }

    @Test
    public void addUserWithEmptyName() throws ValidationException{
        User user = User.builder()
                    .email("mail@ya.ru")
                    .name("")
                    .login("Where'sTheMoney")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();
        
        controller.addUser(user);

        ArrayList<User> arr = new ArrayList<>(controller.getUsers());
        assertEquals(arr.get(0).getName(), "Where'sTheMoney");
        assertEquals(arr.get(0).getEmail(), "mail@ya.ru");
        assertEquals(arr.get(0).getLogin(), "Where'sTheMoney");
        assertEquals(arr.get(0).getBirthday(), LocalDate.of(1942, 12, 4));
        assertEquals(arr.get(0).getId(), 1);        
    }
}
