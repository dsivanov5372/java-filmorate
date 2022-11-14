package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDao;
import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class UserDaoTest {
    private final UserDao userDao;

    @Autowired
    public UserDaoTest(UserDao userDao) {
        this.userDao = userDao;
    }

    @Test
    public void addUserIfValid() throws ValidationException {
        User user = User.builder()
                        .email("mail@ya.ru")
                        .name("Big Lebowski")
                        .login("Where'sTheMoney")
                        .birthday(LocalDate.of(1942, 12, 4))
                        .build();

        user = userDao.addUser(user);
        assertEquals(user, userDao.getUserById(user.getId()));
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
                        "Wrong login format"),
                Arguments.of(User.builder()
                                .email("email@mail.com")
                                .name("Big Lebowski")
                                .login("login")
                                .birthday(LocalDate.now().plusDays(1))
                                .build(),
                        "Birthday can't be in the future"));
    }

    @ParameterizedTest
    @MethodSource("invalidUsers")
    public void throwExceptionWhenAddUserIfInvalidField(User user, String message) {
        ValidationException exception = assertThrows(ValidationException.class, () -> userDao.addUser(user));
        assertEquals(message, exception.getMessage());
    }

    @ParameterizedTest
    @MethodSource("invalidUsers")
    public void throwExceptionWhenUpdateUserIfInvalidField(User user, String message) throws ValidationException {
        user.setId(1);
        ValidationException exception = assertThrows(ValidationException.class, () -> userDao.updateUser(user));
        assertEquals(message, exception.getMessage());
    }

    @Test
    public void updateUserIfValidFields() throws ValidationException {
        User user1 = User.builder()
                .email("mail@yandex.ru")
                .name("Аркадий Волож")
                .login("ПлачуМногаДеняк")
                .birthday(LocalDate.of(1942, 12, 4))
                .build();

        user1 = userDao.addUser(user1);

        User user2 = User.builder()
                        .id(user1.getId())
                        .email("mail@yandex.ru")
                        .name("Аркадий Волож")
                        .login("ПлачуМалаДеняк")
                        .birthday(LocalDate.of(1942, 12, 4))
                        .build();

        userDao.updateUser(user2);

        assertEquals(user2, userDao.getUserById(user2.getId()));
    }

    @Test
    public void returnListOfUserIfContainsThem() throws ValidationException {
        User user1 = User.builder()
                .email("fired_from_yandex@yandex.ru")
                .name("Аркадий Волож")
                .login("ПлачуCколькоТоДеняк")
                .birthday(LocalDate.of(1942, 12, 4))
                .build();

        user1 = userDao.addUser(user1);

        User user2 = User.builder()
                .email("yandex@yandex.ru")
                .name("Аркадий Волож")
                .login("НеПлачуДеняк")
                .birthday(LocalDate.of(1942, 12, 4))
                .build();

        user2 = userDao.addUser(user2);

        Collection<User> users = userDao.getUsers();

        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
    }


}
