package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@lombok.Data
@lombok.Builder
public class Film {
    @Min(0)
    int id;
    @NotNull
    @NotBlank
    String name;
    @Size(max=200)
    String description;
    LocalDate releaseDate;
    @Min(1)
    int duration;
}
