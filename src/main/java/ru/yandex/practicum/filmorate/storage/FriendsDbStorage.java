package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class FriendsDbStorage implements FriendsDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id) VALUES(?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public Collection<User> findCommonFriends(long userId, long friendId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
        SqlRowSet result1 = jdbcTemplate.queryForRowSet(sql, userId);
        Collection<Long> friendsOfUser = new ArrayList<>();
        while (result1.next()) {
            friendsOfUser.add(result1.getLong("friend_id"));
        }

        SqlRowSet result2 = jdbcTemplate.queryForRowSet(sql, friendId);
        Collection<User> toReturn = new ArrayList<>();

        while (result2.next()) {
            long id = result2.getLong("friend_id");
            String search = "SELECT * FROM users WHERE user_id = ?";
            SqlRowSet resultOfSearch = jdbcTemplate.queryForRowSet(search, id);

            if (friendsOfUser.contains(id)) {
                resultOfSearch.next();
                toReturn.add(User.builder().id(resultOfSearch.getLong("user_id"))
                        .email(resultOfSearch.getString("email"))
                        .login(resultOfSearch.getString("login"))
                        .name(resultOfSearch.getString("name"))
                        .birthday(resultOfSearch.getDate("birthday").toLocalDate()).build());
            }
        }

        return toReturn;
    }

    @Override
    public Collection<User> getUserFriends(long userId) {
        String sql = "SELECT * FROM users WHERE user_id IN (SELECT friend_id FROM friends WHERE user_id = ?)";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, userId);
        Collection<User> toReturn = new ArrayList<>();

        while (result.next()) {
            User user = User.builder()
                    .id(result.getLong("user_id"))
                    .email(result.getString("email"))
                    .login(result.getString("login"))
                    .name(result.getString("name"))
                    .birthday(result.getDate("birthday").toLocalDate())
                    .build();
            toReturn.add(user);
        }

        return toReturn;
    }
}
