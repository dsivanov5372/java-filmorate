package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
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
    @NotNull
    MpaRating mpa;
    Set<Genre> genres = new HashSet<>();
}
