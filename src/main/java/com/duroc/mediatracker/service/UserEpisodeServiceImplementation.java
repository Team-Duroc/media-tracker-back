package com.duroc.mediatracker.service;

import com.duroc.mediatracker.Exception.InvalidItemException;
import com.duroc.mediatracker.Exception.ItemNotFoundException;
import com.duroc.mediatracker.model.info.Episode;
import com.duroc.mediatracker.model.user.AppUser;
import com.duroc.mediatracker.model.user.UserEpisode;
import com.duroc.mediatracker.model.user.UserEpisodeId;
import com.duroc.mediatracker.repository.UserEpisodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserEpisodeServiceImplementation implements UserEpisodeService {

    @Autowired
    UserEpisodeRepository userEpisodeRepository;

    @Autowired
    EpisodeService episodeService;

    @Autowired
    UserService userService;

    @Override
    public List<UserEpisode> getUserEpisodeListByShowId(Long showId) {
        AppUser user = userService.getUser();
        List<UserEpisode> allUserEpisodes = userEpisodeRepository.findByUserEpisodeIdAppUser(user);
        return allUserEpisodes.stream().filter(ep -> Objects.equals(ep.getUserEpisodeId().getEpisode().getShow().getId(), showId)).toList();
    }

    @Override
    public List<UserEpisode> saveAllShowEpisodesAsUserEpisodes(Long showId) {
        AppUser user = userService.getUser();
        List<Episode> showEpisodes = episodeService.getSavedEpisodesByShowId(showId);
        List<UserEpisode> userEpisodeList = new ArrayList<>();
        showEpisodes.forEach((ep) ->
        {
            UserEpisodeId userEpisodeId = new UserEpisodeId(user, ep);
            UserEpisode userEpisode = new UserEpisode(userEpisodeId, 0, "", false, null);
            userEpisodeList.add(userEpisode);
            userEpisodeRepository.save(userEpisode);
        });
        return userEpisodeList;
    }

    @Override
    public UserEpisode getUserEpisodeByEpisodeId(Long episodeId) {
        AppUser user = userService.getUser();
        Episode episode = episodeService.getSavedEpisodeById(episodeId);
        UserEpisodeId userEpisodeId = new UserEpisodeId(user, episode);
        if(userEpisodeRepository.findById(userEpisodeId).isPresent()) {
            return userEpisodeRepository.findById(userEpisodeId).get();
        } else {
            throw new ItemNotFoundException("Could not find user episode with specified id");
        }}


    @Override
    public UserEpisode changeUserEpisodeDetails(Long episodeId, UserEpisode newUserEpisode) {
        UserEpisode userEpisode = getUserEpisodeByEpisodeId(episodeId);
        if(newUserEpisode.getUserEpisodeId() == null) {
            newUserEpisode.setUserEpisodeId(userEpisode.getUserEpisodeId());
        }
        if(!Objects.equals(newUserEpisode.getUserEpisodeId(), userEpisode.getUserEpisodeId())) {
            throw new InvalidItemException("User and Episode properties are not changeable");
        } else {
            userEpisode.setNotes(newUserEpisode.getNotes());
            if(newUserEpisode.isWatched() && !userEpisode.isWatched()) {
                userEpisode.setWatchedDate(LocalDate.now());
            }
            userEpisode.setWatched(newUserEpisode.isWatched());
            userEpisode.setRating(newUserEpisode.getRating());
            return userEpisodeRepository.save(userEpisode);
        }
    }


    @Override
    public int getAllRuntimeWatched() {
        AppUser user = userService.getUser();
        List<UserEpisode> allUserEpisodes = userEpisodeRepository.findByUserEpisodeIdAppUser(user);
        int totalWatchedRuntime = 0;
        for(UserEpisode userEpisode : allUserEpisodes) {
            if(userEpisode.isWatched()) {
                totalWatchedRuntime += userEpisode.getUserEpisodeId().getEpisode().getRuntime();
            }
        }
        return totalWatchedRuntime;
    }
}

