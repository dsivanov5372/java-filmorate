package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@lombok.Data
@lombok.Builder
public class User {
    @Min(0)
    int id;
    @Email
    String email;
    String name;
    @NotNull
    @NotBlank
    String login;
    LocalDate birthday;
}
