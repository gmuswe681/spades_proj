package com.spades.spades.resources;

import com.spades.spades.model.Games;
import com.spades.spades.model.Users;
import com.spades.spades.repository.GamesRepository;
import com.spades.spades.repository.UsersRepository;
import com.spades.spades.service.GetAuthenticationService;
import com.spades.spades.service.SpadesGameService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

@RequestMapping("/secured/all/game")
@RestController
public class ActiveGameController {

    @Autowired
    private GetAuthenticationService authService;

    @Autowired
    private SpadesGameService spadesService;

    private final GamesRepository gamesRepository;
    private final UsersRepository usersRepository;

    private static final Logger LOGGER = LogManager.getLogger("ActiveGameController.class");

    ActiveGameController(GamesRepository g, UsersRepository u)
    {
        gamesRepository = g;
        usersRepository = u;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/{gameid}", method = RequestMethod.GET)
    public String updateGame(@PathVariable int gameid)
    {
        Optional<Games> foundGame = gamesRepository.findByGameId(gameid);
        if(foundGame.isPresent())
        {
            Games currGame = foundGame.get();
            if(currGame.getGameStatus().equals("o"))
            {
                LOGGER.debug("This game is waiting for players.");
                return generateHtmlResponse("Waiting for a player to join. Refresh the page.");
            }

            if(!currGame.getGameStatus().equals("a"))
            {
                LOGGER.debug("This game is not active.");
                return generateHtmlResponse("Invalid");
            }

            int playerId = findPlayerID();
            if(playerId == currGame.getPlayer1Id() || playerId == currGame.getPlayer2Id())
            {
                String response = spadesService.progressGame(gameid, playerId);
                return generateHtmlResponse(response);
            }
            else
            {
                LOGGER.debug("This user is not part of this game.");
                return generateHtmlResponse("Invalid");
            }
        }
        else
        {
            LOGGER.debug("Game ID not found");
            return generateHtmlResponse("Invalid");
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

    private String generateHtmlResponse(String s)
    {
        String result = "<html>\n";
        result += "<head></head>\n";
        result += "<body>\n";

        result += s + "\n";

        result += "</body>\n";
        result += "</html>";
        return result;
    }
}