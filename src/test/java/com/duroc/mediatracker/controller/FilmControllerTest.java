package com.duroc.mediatracker.controller;

import com.duroc.mediatracker.model.film_details.FilmDetails;
import com.duroc.mediatracker.model.film_details.Genre;
import com.duroc.mediatracker.model.film_details.ProductionCompany;
import com.duroc.mediatracker.model.film_search.FilmSearchResults;
import com.duroc.mediatracker.model.film_search.Result;
import com.duroc.mediatracker.model.info.Film;
import com.duroc.mediatracker.service.FilmServiceImplementation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    @Mock
    private FilmServiceImplementation filmServiceImplementation;

    @InjectMocks
    private FilmController filmController;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;

    @BeforeEach
    public void setup(){
        mockMvc = MockMvcBuilders.standaloneSetup(filmController).build();
        mapper = new ObjectMapper();
    }

    @Test
    void testGetFilmSearchResults() throws Exception {
        // arrange
        String query = "movie";
        FilmSearchResults mockResults = new FilmSearchResults(
                List.of(
                        new Result(1L, "overview", "/poster1.jpg", "2010-07-16", "movie"),
                        new Result(2L, "overview", "/poster2.jpg", "2014-11-07", "movie 2")
                ),
                2
        );
        when(filmServiceImplementation.getFilmSearchResults(query)).thenReturn(mockResults);

        // act and assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/mediatracker/films/search/{query}", query))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].title").value("movie"))
                .andExpect(jsonPath("$[1].title").value("movie 2"));

        verify(filmServiceImplementation).getFilmSearchResults(query);
    }

    @Test
    void testGetFilmDetails() throws Exception {
        // arrange
        Long movieID = 123L;
        FilmDetails mockFilmDetails = new FilmDetails(
                List.of(new Genre("Action")),
                "en",
                List.of("US"),
                "An action-packed adventure.",
                "/sample_poster_path.jpg",
                List.of(new ProductionCompany("Sample Production")),
                "2023-01-01",
                120,
                "Sample Movie"
        );
        when(filmServiceImplementation.getFilmDetails(movieID)).thenReturn(mockFilmDetails);

        // act and assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/mediatracker/films/details/{movieID}", movieID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Sample Movie"))
                .andExpect(jsonPath("$.runtime").value(120));

        verify(filmServiceImplementation).getFilmDetails(movieID);
    }

    @Test
    void testAddFilmById() throws Exception {
        // arrange
        Long movieID = 123L;
        Film mockFilm = Film.builder().id(1L).title("Sample Movie").synopsis("An action-packed adventure.").releaseYear(2023).duration(120).genres(List.of("Action"))
                .productionCompanies(List.of("Sample Production")).language("en").country("US").poster_url("/sample_poster_path.jpg").build();
        when(filmServiceImplementation.addFilmToList(movieID)).thenReturn(mockFilm);

        // act and assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/mediatracker/films/save/{movieID}", movieID))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Sample Movie"))
                .andExpect(jsonPath("$.id").value(1L));

        verify(filmServiceImplementation).addFilmToList(movieID);
    }

    @Test
    void testGetFilmById() throws Exception {
        // arrange
        Long id = 1L;
        Film mockFilm = Film.builder().id(1L).title("Sample Movie").synopsis("An action-packed adventure.").releaseYear(2023).duration(120).genres(List.of("Action"))
                .productionCompanies(List.of("Sample Production")).language("en").country("US").poster_url("/sample_poster_path.jpg").build();
        when(filmServiceImplementation.getFilmById(id)).thenReturn(Optional.of(mockFilm));

        // act and assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/mediatracker/films/saved/{Id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Sample Movie"))
                .andExpect(jsonPath("$.id").value(1L));

        verify(filmServiceImplementation).getFilmById(id);
    }

    @Test
    void testDeleteFilmById() throws Exception {
        // arrange
        Long id = 1L;
        doNothing().when(filmServiceImplementation).deleteFilmById(id);

        // act and assert
        mockMvc.perform(delete("/api/v1/mediatracker/films/delete/{Id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Film successfully deleted"));

        verify(filmServiceImplementation).deleteFilmById(id);

    }
}