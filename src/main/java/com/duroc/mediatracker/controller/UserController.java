package com.duroc.mediatracker.controller;

import com.duroc.mediatracker.model.user.*;
import com.duroc.mediatracker.service.UserEpisodeService;
import com.duroc.mediatracker.service.UserFilmService;
import com.duroc.mediatracker.service.UserService;
import com.duroc.mediatracker.service.UserShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;

// Token is sent from the backend regardless of signing up with us or already being a user
// some sort of check logic is needed to see if they need to be a user on our db
// Potentially (Just have a method to check to see if the user is already on the local db)
    // Have some sort of entry endpoint for when a user opens the app
    // (Have a token sent from the front to the back, and we have a verify endpoint to see if the user is already on the db)
    // Or the first endpoint that is most likely to be called will have that check
@RestController
@RequestMapping("/api/v1/mediatracker/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserShowService userShowService;

    @Autowired
    UserEpisodeService userEpisodeService;

    @Autowired
    UserFilmService userFilmService;

    /**
     * todo:
     *      S̶p̶r̶i̶n̶g̶ S̶e̶c̶u̶r̶i̶t̶y̶ s̶e̶t̶ u̶p̶ f̶o̶r̶ t̶h̶e̶ "u̶s̶e̶r̶" e̶n̶d̶p̶o̶i̶n̶t̶
     *      (Potentially) refactor Show and Film endpoint('s) for user specified things:
     *         - move them to the User Controller
     *      F̶i̶l̶t̶e̶r̶ f̶o̶r̶ t̶h̶e̶ e̶n̶d̶p̶o̶i̶n̶t̶ t̶h̶a̶t̶ c̶a̶n̶ b̶e̶ a̶p̶p̶l̶i̶e̶d̶ t̶o̶ S̶p̶r̶i̶n̶g̶ S̶e̶c̶u̶r̶i̶t̶y̶
     *      Specific endpoint for adding a User to the database (eg: they just signed up) -> something like authenticateUserIsInDatabase
     *          - + all logic that comes with that (Service, Repo, Tests etc..)
     */


    // Ths is an example of how an endpoint can look like for receiving a token from the client and checking the user in the db

    @GetMapping("/auth")
    public ResponseEntity<AppUser> authenticateUserIsInDatabase() {
        // ... call whatever userService method needed
        userService.getUser();
        // HttpStatus.OK only needs to be sent in this case because if anything goes wrong then an Exception should be thrown and
        // the Global exception handler should deal with it
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<AppUser> getUserById(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.getUserById(userId), HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser appUser) {
        return new ResponseEntity<>(userService.saveUser(appUser), HttpStatus.OK);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<AppUser> changeUsername(@PathVariable Long userId, @RequestBody String newUsername) {
        return new ResponseEntity<>(userService.changeUsername(userId, newUsername), HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.deleteUser(userId), HttpStatus.OK);
    }

    @GetMapping("/{userId}/shows/")
    public ResponseEntity<List<UserShow>> getShowsFromUserList(@PathVariable Long userId) throws IOException, InterruptedException {
        return new ResponseEntity<>(userShowService.getAllShowsFromUserList(userId), HttpStatus.OK);
    }

    @PostMapping("/{userId}/shows/save/{apiShowId}")
    public ResponseEntity<UserShow> saveShowToUserList(@RequestBody UserShow userShow,
                                                       @PathVariable Long userId, @PathVariable Long apiShowId) throws IOException, InterruptedException {
        return new ResponseEntity<>(userShowService.saveShowToUserList(userShow, userId, apiShowId), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/shows/{showId}")
    public ResponseEntity<UserShow> getUserShowByShowId(@PathVariable Long userId, @PathVariable Long showId) {
        return new ResponseEntity<>(userShowService.getUserShowByShowId(userId, showId), HttpStatus.OK);
    }

    @GetMapping("/{userId}/shows")
    public ResponseEntity<List<UserShow>> getUserShowsByWatchStatusAndOptionalGenre(@PathVariable Long userId,
                                                                                    @RequestParam String status, @RequestParam(required = false) String genre) {
        return new ResponseEntity<>(userShowService.getUserShowsByWatchStatusAndOptionalGenre(userId, status, genre), HttpStatus.OK);
    }

    @PutMapping("/{userId}/shows/{showId}")
    public ResponseEntity<UserShow> changeUserShowDetails(@PathVariable Long userId, @PathVariable Long showId,
                                                          @RequestBody UserShow newUserShow) {
        return new ResponseEntity<>(userShowService.changeUserShowDetails(userId, showId, newUserShow), HttpStatus.OK);
    }


    @GetMapping("/{userId}/shows/{showId}/episodes")
    public ResponseEntity<List<UserEpisode>> getUserEpisodeListByShowId(@PathVariable Long userId, @PathVariable Long showId) {
        return new ResponseEntity<>(userEpisodeService.getUserEpisodeListByShowId(userId, showId), HttpStatus.OK);
    }

    @GetMapping("/{userId}/episode/{episodeId}")
    public ResponseEntity<UserEpisode> getUserEpisodeByEpisodeId(@PathVariable Long userId, @PathVariable Long episodeId) {
        return new ResponseEntity<>(userEpisodeService.getUserEpisodeByEpisodeId(userId, episodeId), HttpStatus.OK);
    }

    @PutMapping("/{userId}/episode/{episodeId}")
    public ResponseEntity<UserEpisode> changeUserEpisodeDetails(@PathVariable Long userId, @PathVariable Long episodeId,
                                                                @RequestBody UserEpisode newUserEpisode) {
        return new ResponseEntity<>(userEpisodeService.changeUserEpisodeDetails(userId, episodeId, newUserEpisode), HttpStatus.OK);
    }

    // UserFilm methods
    @PostMapping("/{userId}/films/{movieId}")
    public ResponseEntity<UserFilm> saveUserFilm(@PathVariable Long userId, @PathVariable Long movieId, @RequestBody UserFilm userFilm) throws IOException, InterruptedException {
        UserFilm savedUserFilm = userFilmService.saveUserFilm(userFilm, userId, movieId);
        return new ResponseEntity<>(savedUserFilm, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/films")
    public ResponseEntity<List<UserFilm>> getUserFilms(@PathVariable Long userId) {
        List<UserFilm> userFilms = userFilmService.getAllUserFilms(userId);
        return new ResponseEntity<>(userFilms,HttpStatus.OK);
    }

    @PatchMapping("/{userId}/films/{filmDbId}")
    public ResponseEntity<UserFilm> updateUserFilm(@PathVariable Long userId, @PathVariable Long filmDbId, @RequestBody UserFilm updatedUserFilm) {
        UserFilm userFilm = userFilmService.updateUserFilm(updatedUserFilm, userId, filmDbId);
        return new ResponseEntity<>(userFilm, HttpStatus.OK);
    }

    @GetMapping("/{userId}/films/{filmDbId}")
    public ResponseEntity<UserFilm> getUserFilmById(@PathVariable Long userId, @PathVariable Long filmDbId) {
        UserFilm userFilm = userFilmService.getUserFilmById(userId, filmDbId);
        return new ResponseEntity<>(userFilm, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}/films/{filmDbId}")
    public ResponseEntity<String> deleteUserFilmById(@PathVariable Long userId, @PathVariable Long filmDbId) {
        userFilmService.deleteUserFilmById(userId, filmDbId);
        return new ResponseEntity<>("UserFilm successfully deleted", HttpStatus.OK);
    }

    @GetMapping("/{userId}/films/search")
    public ResponseEntity<?> getUserFilmsByStatus(@PathVariable Long userId, @RequestParam Status status) {
        List<UserFilm> userFilms = userFilmService.getUserFilmsByStatus(userId, status);
        if(userFilms.isEmpty()) {
            return new ResponseEntity<>("No films found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(userFilms, HttpStatus.OK);
    }

    @GetMapping("/{userId}/totalWatchedRuntime")
    public ResponseEntity<Integer> getTotalWatchedRuntime(@PathVariable Long userId) {
        Integer totalRuntime = userService.totalRuntime(userId);

        return new ResponseEntity<>(totalRuntime, HttpStatus.OK);
    }

    @GetMapping("/{userId}/genreStats")
    public ResponseEntity<Map<String,Integer>> getGenreStats(@PathVariable Long userId) {
        return new ResponseEntity<>(userService.getAllByGenre(userId),HttpStatus.OK);
    }
}
