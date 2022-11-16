package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.util.Collection;
import javax.validation.Valid;

@RestController
public class FilmController {
    private final FilmService service;
    
    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    @GetMapping("/films")
    public Collection<Film> getFilms() {
        return service.getFilms();
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) throws ValidationException {
        return service.addFilm(film);
    }

    @PutMapping("/films") 
    public Film updateFilm(@Valid @RequestBody Film film) 
                throws ValidationException, FilmNotFoundException {
        return service.updateFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable("id") String filmId, 
                        @PathVariable("userId") String userId) 
                        throws FilmNotFoundException {
        service.addLike(Long.parseLong(filmId), Long.parseLong(userId));
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") String filmId, 
                           @PathVariable("userId") String userId) 
                           throws FilmNotFoundException {
        service.removeLike(Long.parseLong(filmId), Long.parseLong(userId));
    }

    @GetMapping("/films/popular")
    public Collection<Film> getMostPopularFilms(@RequestParam(defaultValue = "10") String count) {
        return service.getMostPopularFilms(Integer.parseInt(count));
    }

    @GetMapping("/films/{id}")
    public Film getFilm(@PathVariable("id") String filmId) throws FilmNotFoundException {
        return service.getFilm(Long.parseLong(filmId));
    }
}