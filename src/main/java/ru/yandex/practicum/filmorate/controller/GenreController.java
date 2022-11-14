package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
public class GenreController {
    private final GenreService service;

    @Autowired
    public GenreController(GenreService service) {
        this.service = service;
    }

    @GetMapping("/genres")
    public Collection<Genre> getGenres() {
        return service.getGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenre(@PathVariable("id") String genreId) throws RuntimeException {
        return service.getGenre(Integer.parseInt(genreId));
    }
}
