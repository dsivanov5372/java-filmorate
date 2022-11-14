package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@RestController
public class MpaController {
    private final MpaService service;

    @Autowired
    public MpaController(MpaService service) {
        this.service = service;
    }

    @GetMapping("/mpa")
    public Collection<MpaRating> getMpaRatings() {
        return service.getMpaRatings();
    }

    @GetMapping("/mpa/{id}")
    public MpaRating getMpaRating(@PathVariable("id") String mpaId) throws RuntimeException {
        return service.getMpaRating(Integer.parseInt(mpaId));
    }
}
