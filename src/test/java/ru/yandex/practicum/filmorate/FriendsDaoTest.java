package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsDao;
import ru.yandex.practicum.filmorate.storage.UserDao;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
public class FriendsDaoTest {
    private final FriendsDao friendsDao;
    private final UserDao userDao;

    @Autowired
    public FriendsDaoTest(FriendsDao friendsDao, UserDao userDao) {
        this.friendsDao = friendsDao;
        this.userDao = userDao;
    }

    @Test
    public void returnEmptyListOfFriendsIfLonely() throws ValidationException {
        User user = User.builder()
                        .email("mail101@yandex.ru")
                        .name("Аркадий Волож")
                        .login("login101")
                        .birthday(LocalDate.of(1942, 12, 4))
                        .build();

        user = userDao.addUser(user);

        assertTrue(friendsDao.getUserFriends(user.getId()).isEmpty());

        User newUser = User.builder()
                            .email("mail102@yandex.ru")
                            .name("Илья Сегалович")
                            .login("login102")
                            .birthday(LocalDate.of(1942, 12, 4))
                            .build();

        newUser = userDao.addUser(newUser);
        friendsDao.addFriend(user.getId(), newUser.getId());
        friendsDao.deleteFriend(user.getId(), newUser.getId());

        assertTrue(friendsDao.getUserFriends(user.getId()).isEmpty());
    }

    @Test
    public void returnEmptyListOfFriendsIfHasFriends() throws ValidationException {
        User user1 = User.builder()
                        .email("mail103@yandex.ru")
                        .name("Аркадий Волож")
                        .login("login103")
                        .birthday(LocalDate.of(1942, 12, 4))
                        .build();

        user1 = userDao.addUser(user1);

        User user2 = User.builder()
                        .email("mail104@yandex.ru")
                        .name("Аркадий Волож")
                        .login("login104")
                        .birthday(LocalDate.of(1942, 12, 4))
                        .build();

        user2 = userDao.addUser(user2);

        friendsDao.addFriend(user1.getId(), user2.getId());
        assertTrue(friendsDao.getUserFriends(user1.getId()).contains(user2));
    }

    @Test
    public void returnEmptyListIfNoCommonFriends() throws ValidationException {
        User user1 = User.builder()
                .email("mail105@yandex.ru")
                .name("Аркадий Волож")
                .login("login105")
                .birthday(LocalDate.of(1942, 12, 4))
                .build();

        user1 = userDao.addUser(user1);

        User user2 = User.builder()
                .email("mail106@yandex.ru")
                .name("Илья Сегалович")
                .login("login106")
                .birthday(LocalDate.of(1942, 12, 4))
                .build();

        user2 = userDao.addUser(user2);

        assertTrue(friendsDao.findCommonFriends(user1.getId(), user2.getId()).isEmpty());
    }

    @Test
    public void returnListOfCommonFriendsIfTheyHaveThem() throws ValidationException {
        User user1 = User.builder()
                    .email("mail107@yandex.ru")
                    .name("Сергей Брин")
                    .login("login107")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();

        user1 = userDao.addUser(user1);

        User user2 = User.builder()
                    .email("mail108@yandex.ru")
                    .name("Ларри Пейдж")
                    .login("login108")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();

        user2 = userDao.addUser(user2);

        User user3 = User.builder()
                    .email("mail109@yandex.ru")
                    .name("Марк Цукерберг")
                    .login("login109")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();

        user3 = userDao.addUser(user3);

        friendsDao.addFriend(user1.getId(), user3.getId());
        friendsDao.addFriend(user2.getId(), user3.getId());

        assertTrue(friendsDao.findCommonFriends(user1.getId(), user2.getId()).contains(user3));
    }
}