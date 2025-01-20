package com.duroc.mediatracker.service;

import com.duroc.mediatracker.model.user.AppUser;

import java.util.Map;

public interface UserService {
    AppUser getUserById(Long id);
    void getUser();
    AppUser saveUser(AppUser appUser);
    AppUser changeUsername(Long userId, String newUsername);
    String deleteUser(Long userId);

    Map<String,Integer> getAllByGenre (Long userId);

    int totalRuntime(Long userId);
}
