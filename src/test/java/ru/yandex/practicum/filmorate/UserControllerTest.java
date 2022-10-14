package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Stream;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;

public class UserControllerTest {
    private UserController controller;

    @BeforeEach
    public void createController() {
        controller = new UserController(new UserService(new InMemoryUserStorage()));
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

    @Test
    public void addFriendIfHasUsers() throws ValidationException {
        User user1 = User.builder()
                    .email("mail@ya.ru")
                    .name("Big Lebowski")
                    .login("Where'sTheMoney")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();
        User user2 = User.builder()
                    .email("mail@ya.ru")
                    .name("Big Lebowski")
                    .login("Where'sTheMoney")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();

        controller.addUser(user1);
        controller.addUser(user2);
        controller.addFriend(String.valueOf(user1.getId()), String.valueOf(user2.getId()));

        assertTrue(user1.getFriends().contains(user2.getId()));
        assertTrue(user2.getFriends().contains(user1.getId()));
    }

    @Test
    public void deleteFriendIfHasIt() throws ValidationException, RuntimeException {
        User user1 = User.builder()
                    .email("mail@ya.ru")
                    .name("Big Lebowski")
                    .login("Where'sTheMoney")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();
        User user2 = User.builder()
                    .email("mail@ya.ru")
                    .name("Big Lebowski")
                    .login("Where'sTheMoney")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();

        controller.addUser(user1);
        controller.addUser(user2);
        controller.addFriend(String.valueOf(user1.getId()), String.valueOf(user2.getId()));
        controller.deleteFriend(String.valueOf(user1.getId()), String.valueOf(user2.getId()));

        assertTrue(user1.getFriends().isEmpty());
        assertTrue(user2.getFriends().isEmpty());
    }

    public static Stream<Arguments> usersAndId() {
        User user1 = User.builder()
                    .email("mail@ya.ru")
                    .name("Big Lebowski")
                    .login("Where'sTheMoney")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();
        User user2 = User.builder()
                    .email("mail@ya.ru")
                    .name("Big Lebowski")
                    .login("Where'sTheMoney")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();

        return Stream.of(Arguments.of(user1, user2, "1", "3"),
                         Arguments.of(user1, user2, "3", "2"));        
    }

    @ParameterizedTest
    @MethodSource("usersAndId")
    public void throwsExceptionIdDoesNotHaveSuchUserWhenAddFriend(User user1, User user2, String id1, String id2) 
                throws ValidationException {
        controller.addUser(user1);
        controller.addUser(user2);
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            controller.addFriend(id1, id2);
        });

        assertEquals(exception.getMessage(), "Wrong user id");
        assertTrue(user1.getFriends().isEmpty());
        assertTrue(user2.getFriends().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("usersAndId")
    public void throwsExceptionIdDoesNotHaveSuchUserWhenRemoveFriend(User user1, User user2, String id1, String id2) 
            throws ValidationException {
        controller.addUser(user1);
        controller.addUser(user2);
        controller.addFriend(String.valueOf(user1.getId()), String.valueOf(user2.getId()));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> { 
            controller.deleteFriend(id1, id2);
        });

        assertEquals(exception.getMessage(), "Wrong user id");
        assertTrue(user1.getFriends().contains(user2.getId()));
        assertTrue(user2.getFriends().contains(user1.getId()));
    }

    @ParameterizedTest
    @MethodSource("usersAndId")
    public void throwsExceptionIfDoesNotHaveSuchUsersWhenFindCommonFriends(
        User user1, User user2, String id1, String id2) throws ValidationException {
        controller.addUser(user1);
        controller.addUser(user2);
        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> { 
            controller.findCommonFriends(id1, id2);
        });

        assertEquals(exception.getMessage(), "Wrong user id");
        assertTrue(user1.getFriends().isEmpty());
        assertTrue(user2.getFriends().isEmpty());
    }

    @Test
    public void findCommonFriendIfHasUsers() throws RuntimeException, ValidationException {
        User user1 = User.builder()
                    .email("mail@ya.ru")
                    .name("Big Lebowski")
                    .login("Where'sTheMoney")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();
        User user2 = User.builder()
                    .email("mail@ya.ru")
                    .name("Big Lebowski")
                    .login("Where'sTheMoney")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();
        User user3 = User.builder()
                    .email("mail@ya.ru")
                    .name("Big Lebowski")
                    .login("Where'sTheMoney")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();
        
        controller.addUser(user1);
        controller.addUser(user2);
        controller.addUser(user3);
        controller.addFriend(String.valueOf(user1.getId()), String.valueOf(user2.getId()));
        controller.addFriend(String.valueOf(user1.getId()), String.valueOf(user3.getId()));
        controller.addFriend(String.valueOf(user2.getId()), String.valueOf(user3.getId()));

        ArrayList<User> commonFriend = new ArrayList<>(
                        controller.findCommonFriends(String.valueOf(user1.getId()), 
                                                    String.valueOf(user2.getId())));
        
        assertEquals(commonFriend.size(), 1);
        assertEquals(commonFriend.get(0), user3);
    }

    public static Stream<Arguments> usersWithFriends() {
        User user1 = User.builder()
                    .email("mail@ya.ru")
                    .name("Big Lebowski")
                    .login("Where'sTheMoney")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();
        User user2 = User.builder()
                    .email("mail@ya.ru")
                    .name("Big Lebowski")
                    .login("Where'sTheMoney")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();
        User user3 = User.builder()
                    .email("mail@ya.ru")
                    .name("Big Lebowski")
                    .login("Where'sTheMoney")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();
        User user4 = User.builder()
                    .email("mail@ya.ru")
                    .name("Big Lebowski")
                    .login("Where'sTheMoney")
                    .birthday(LocalDate.of(1942, 12, 4))
                    .build();
        user3.setFriends(new HashSet<>());
        user4.setFriends(new HashSet<>());                    
        user3.addFriend(2L);
        user4.addFriend(1L);

        return Stream.of(Arguments.of(user1, user2, 0),
                         Arguments.of(user3, user4, 1));
    }

    @ParameterizedTest
    @MethodSource("usersWithFriends")
    public void shouldReturnListOfFriendsIfHasUsers(User user1, User user2, int count) 
        throws ValidationException, RuntimeException {
        controller.addUser(user1);
        controller.addUser(user2);

        assertEquals(controller.getUserFrinds(String.valueOf(user1.getId())).size(), count);
        assertEquals(controller.getUserFrinds(String.valueOf(user2.getId())).size(), count);
    }

    @Test
    public void throwsExceptionIfDoesNotHaveSuchUserWhenReturnListOfFriends() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            controller.getUserFrinds("1");
        });

        assertEquals(exception.getMessage(), "Wrong user id");
    }

    @Test
    public void returnUserIfHasIt() throws ValidationException, RuntimeException {
        User user1 = User.builder()
            .email("mail@ya.ru")
            .name("Big Lebowski")
            .login("Where'sTheMoney")
            .birthday(LocalDate.of(1942, 12, 4))
            .build();
        
        controller.addUser(user1);
        assertEquals(controller.getUser(String.valueOf(user1.getId())), user1);
    }

    @Test
    public void throwsExceptionIfDoesNotHaveSuchUser() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            controller.getUser("1");
        });

        assertEquals(exception.getMessage(), "Wrong user id");
    }
}