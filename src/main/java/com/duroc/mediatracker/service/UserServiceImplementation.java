package com.duroc.mediatracker.service;

import com.duroc.mediatracker.Exception.InvalidItemException;
import com.duroc.mediatracker.Exception.ItemNotFoundException;
import com.duroc.mediatracker.model.user.AppUser;
import com.duroc.mediatracker.repository.UserRepository;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImplementation implements UserService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    @Lazy
    UserFilmService userFilmService;

    @Autowired
    @Lazy
    UserShowService userShowService;

    @Autowired
    @Lazy
    UserEpisodeService userEpisodeService;

    @Override
    public AppUser getUser(){
//         How to get the details of the user
//         We use SecurityContextHolder.getContext() to get the specific token for the current request being handled,
//         .getAuthentication() gets the FirebaseAuthenticationToken
//         We can now call the .getPrinciple() method and cast it to a FirebaseToken
//         We can cast it without worry because we know what the Principle Object is (see getPrinciple() in the FirebaseAuthenticationToken.class)

        FirebaseToken token = (FirebaseToken) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<AppUser> appUsers = userRepository.findByUserToken(token.getUid());
        if(appUsers.isEmpty()){
            return saveUser(token.getUid());
        }
        else{
            return  appUsers.getFirst();
        }
//        // From that token we can get some details about the user
//        // Their name
//        System.out.println(token.getName());
//        // The Unique Identifier (our OAuthId field)
//        System.out.println(token.getUid());
//        // Their profile picture url
//        System.out.println(token.getPicture());
//        // Their email
//        System.out.println(token.getEmail());
//
//        ... some logic to see if the user is in the db or not (use the Uid to get the user in the db)

    };
    @Override
    public AppUser getUserById(Long id) {
        if(userRepository.findById(id).isPresent()) {
            return userRepository.findById(id).get();
        } else {
            throw new ItemNotFoundException(String.format("User with id %s not found", id));
        }
    }

    @Override
    public AppUser saveUser(String token) {
        AppUser appUser = AppUser.builder().userToken(token).build();
        return userRepository.save(appUser);
    }

//    @Override
//    public AppUser changeUsername(Long userId, String newUsername) {
//        AppUser user = getUserById(userId);
//        if(newUsername.isEmpty()) {
//            throw new InvalidItemException("Username cannot be empty");
//        } else {
//            user.setUsername(newUsername);
//            return userRepository.save(user);
//        }
//    }

    @Override
    public String deleteUser() {
        AppUser user = getUser();
        userRepository.delete(user);
        return "User deleted successfully";

    }

    @Override
    public Map<String, Integer> getAllByGenre() {
        Map<String, Integer> filmGenreMap = userFilmService.getStatsForFilmGenres();
        Map<String, Integer> showGenreMap = userShowService.getNumberOfShowsWatchedByGenre();

        Map<String, Integer> resultMap = new HashMap<>(filmGenreMap);
        for(String genre : showGenreMap.keySet()) {
            if(!resultMap.containsKey(genre)){
                resultMap.put(genre,showGenreMap.get(genre));
            }
            else{
                resultMap.put(genre, resultMap.get(genre)+showGenreMap.get(genre));
            }
        }
        return resultMap;
    }

    @Override
    public int totalRuntime() {
        int filmRuntime = userFilmService.getUserFilmRuntime();
        int showRuntime = userEpisodeService.getAllRuntimeWatched();
        return filmRuntime+showRuntime;
    }
}
