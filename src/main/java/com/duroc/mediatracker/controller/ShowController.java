package com.duroc.mediatracker.controller;

import com.duroc.mediatracker.model.info.Show;
import com.duroc.mediatracker.model.show_detail.ShowDetails;
import com.duroc.mediatracker.model.show_search.ShowSearchResult;
import com.duroc.mediatracker.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/mediatracker/shows")
public class ShowController {
    @Autowired
    ShowService showService;

    @GetMapping("/search/{searchQuery}")
    public ResponseEntity<ShowSearchResult> getShowSearchResults(@PathVariable String searchQuery) throws IOException, InterruptedException {
        ShowSearchResult searchResults = showService.getShowSearchResults(searchQuery);
        return new ResponseEntity<>(searchResults, HttpStatus.OK);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ShowDetails> getShowDetails(@PathVariable Long id) throws IOException, InterruptedException {
        ShowDetails showDetails = showService.getShowDetails(id);
        return new ResponseEntity<>(showDetails, HttpStatus.OK);
    }

//    @GetMapping("/details2/{id}")
//    public ResponseEntity<Show> getShowDetails2(@PathVariable Long id) throws IOException, InterruptedException {
//        Show showDetails = showService.getShowDetails2(id);
//        return new ResponseEntity<>(showDetails, HttpStatus.OK);
//    }

    @PostMapping("/save")
    public ResponseEntity<Show> saveShowDetails(@RequestBody Long id) throws IOException, InterruptedException {
        Show savedShow = showService.saveShowDetails(id);
        return new ResponseEntity<>(savedShow, HttpStatus.OK);
    }

}
