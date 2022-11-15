package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;

public interface FriendsDao {

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    Collection<User> getUserFriends(long userId);

    Collection<User> findCommonFriends(long userId, long friendId);
}
