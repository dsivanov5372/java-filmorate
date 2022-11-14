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
                        .email("mail@yandex.ru")
                        .name("Аркадий Волож")
                        .login("ПлачуМногаДеняк")
                        .birthday(LocalDate.of(1942, 12, 4))
                        .build();

        user = userDao.addUser(user);

        assertTrue(friendsDao.getUserFriends(user.getId()).isEmpty());

        User newUser = User.builder()
                            .email("notemail@yandex.ru")
                            .name("Илья Сегалович")
                            .login("НеПлачуДеняк")
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
                        .email("mail1@yandex.ru")
                        .name("Аркадий Волож")
                        .login("ПлачуМногаДенякЧестно")
                        .birthday(LocalDate.of(1942, 12, 4))
                        .build();

        user1 = userDao.addUser(user1);

        User user2 = User.builder()
                        .email("mail2@yandex.ru")
                        .name("Аркадий Волож")
                        .login("ПлачуМногаДенякЧестноЧестно")
                        .birthday(LocalDate.of(1942, 12, 4))
                        .build();

        user2 = userDao.addUser(user2);

        friendsDao.addFriend(user1.getId(), user2.getId());
        assertTrue(friendsDao.getUserFriends(user1.getId()).contains(user2));
    }

    @Test
    public void returnEmptyListIfNoCommonFriends() throws ValidationException {
        User user1 = User.builder()
                .email("mail5@yandex.ru")
                .name("Аркадий Волож")
                .login("КаменьНеДам")
                .birthday(LocalDate.of(1942, 12, 4))
                .build();

        user1 = userDao.addUser(user1);

        User user2 = User.builder()
                .email("mail6@yandex.ru")
                .name("Илья Сегалович")
                .login("ПлотинуНадоПоднять")
                .birthday(LocalDate.of(1942, 12, 4))
                .build();

        user2 = userDao.addUser(user2);

        assertTrue(friendsDao.findCommonFriends(user1.getId(), user2.getId()).isEmpty());
    }

    @Test
    public void returnListOfCommonFriendsIfTheyHaveThem() throws ValidationException {
        User user1 = User.builder()
                    .email("mail1@gmail.com")
                    .name("Сергей Брин")
                    .login("ОченьМногоДеняк")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();

        user1 = userDao.addUser(user1);

        User user2 = User.builder()
                    .email("mail2@gmail.com")
                    .name("Ларри Пейдж")
                    .login("НуОченьМногоДеняк")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();

        user2 = userDao.addUser(user2);

        User user3 = User.builder()
                    .email("mail@facebook.com")
                    .name("Марк Цукерберг")
                    .login("НуCлишкомОченьМногоДеняк")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();

        user3 = userDao.addUser(user3);

        friendsDao.addFriend(user1.getId(), user3.getId());
        friendsDao.addFriend(user2.getId(), user3.getId());

        assertTrue(friendsDao.findCommonFriends(user1.getId(), user2.getId()).contains(user3));
    }
}