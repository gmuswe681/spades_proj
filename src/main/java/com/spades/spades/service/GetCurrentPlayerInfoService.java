package com.spades.spades.service;

import java.util.Optional;

import com.spades.spades.model.Users;
import com.spades.spades.repository.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/***
 * Retrieves information about the current player using the current authentication token.
 ***/

@Service
public class GetCurrentPlayerInfoService {

    @Autowired
    private GetAuthenticationService authenticationService;

    @Autowired
    private UsersRepository usersRepository;

    // Retrieves the current player ID, or -1 if not found.
    public int findPlayerId() {
        Authentication a = authenticationService.getAuthentication();
        String user = a.getName();
        Optional<Users> listUser = usersRepository.findByName(user);

        // User was found
        if(listUser.isPresent())
        {
            int playerId = listUser.get().getId();
            return playerId;
        }

        return -1;
    }

    // Retrieves the current player's name.
    public String findPlayerName()
    {
        Authentication a = authenticationService.getAuthentication();
        String user = a.getName();
        return user;
    }
}
