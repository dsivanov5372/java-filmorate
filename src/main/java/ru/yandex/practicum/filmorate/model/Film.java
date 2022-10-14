package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Set;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@lombok.Data
@lombok.Builder
public class Film {
    long id;
    @NotNull
    @NotBlank
    String name;
    @Size(max=200)
    String description;
    LocalDate releaseDate;
    @Min(1)
    int duration;
    Set<Long> usersLikes;

    public void addLike(long userId) {
        usersLikes.add(userId);
    }

    public void deleteLike(long userId) {
        usersLikes.remove(userId);
    }
}
