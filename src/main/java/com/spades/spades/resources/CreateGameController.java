package com.spades.spades.resources;

import com.spades.spades.model.Games;
import com.spades.spades.model.Users;
import com.spades.spades.repository.GamesRepository;
import com.spades.spades.repository.UsersRepository;
import com.spades.spades.service.GetAuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@RequestMapping("/secured/all/creategame")
@RestController
public class CreateGameController {

    @Autowired
    private GetAuthenticationService authService;


    private final GamesRepository gamesRepository;
    private final UsersRepository usersRepository;

    private static final Logger LOGGER = LogManager.getLogger("CreateGameController.class");

    CreateGameController(GamesRepository g, UsersRepository u)
    {
        gamesRepository = g;
        usersRepository = u;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public String createGame()
    {
        int playerID1 = findPlayerID();
        if(playerID1 >= 0)
        {
            createGameInDatabase(playerID1);
            return "game was created";
        }
        else
        {
            return "Unable to create game.";
        }
    }

    private int findPlayerID()
    {
        Authentication a = authService.getAuthentication();
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

    private void createGameInDatabase(int playerID)
    {
        Games g = new Games();
        g.setPlayer1Id(playerID);
        g.setGameStatus("o");
        g.setPointsToWin(100);
        gamesRepository.save(g);
    }
}