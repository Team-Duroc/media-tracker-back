package com.duroc.mediatracker.controller;


import com.duroc.mediatracker.service.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mediatracker/films")
public class FilmController {

    @Autowired
    FilmService filmService;


}
