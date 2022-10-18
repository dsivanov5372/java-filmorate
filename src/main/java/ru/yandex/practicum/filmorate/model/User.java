package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Set;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class User {
    long id;
    @Email
    String email;
    String name;
    @NotNull
    @NotBlank
    String login;
    LocalDate birthday;
    Set<Long> friends;

    public void addFriend(Long id) {
        friends.add(id);
    }

    public void deleteFriend(Long id) {
        friends.remove(id);
    }
}
