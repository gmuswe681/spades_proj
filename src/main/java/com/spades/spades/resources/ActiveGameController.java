package com.spades.spades.resources;

import com.spades.spades.GameTimeOut;
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

    private static GameTimeOut timer;

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
        getTimer(timer);
        if(foundGame.isPresent())
        {
            Games currGame = foundGame.get();
            if(currGame.getGameStatus().equals("o"))
            {
                if(timer.getMessage() == "running"){
                    return getResponse("This game is waiting for players.", "Waiting for a player to join. Refresh the page.");
                } else {
                   return getResponse(timer.getMessage(), timer.getMessage());
                }

            }

            if(!currGame.getGameStatus().equals("a"))
            {
                if(timer.getMessage() == "running") {
                    return getResponse("This game is not active.", "Invalid");
                } else {
                    return getResponse(timer.getMessage(), timer.getMessage());
                }
            }

            int playerId = findPlayerID();
            if(playerId == currGame.getPlayer1Id() || playerId == currGame.getPlayer2Id())
            {
                String response = spadesService.progressGame(gameid, playerId);
                timer.cancelTimeout();
                return generateHtmlResponse(response);
            }
            else
            {
                if(timer.getMessage() == "running") {
                    return getResponse("This user is not part of this game.", "Invalid");
                } else {
                    return getResponse(timer.getMessage(), timer.getMessage());
                }

            }
        }
        else
        {
            if(timer.getMessage() == "running"){
                return getResponse("Game ID not found", "Invalid");
            }   else {
                return getResponse(timer.getMessage(), timer.getMessage());
            }

        }
    }

    public void getTimer(GameTimeOut timer){
        this.timer = timer.getTimeOut();
    }

    private String getResponse(String logResponse, String htmlResponse) {
        LOGGER.debug(logResponse);
        return generateHtmlResponse(htmlResponse);
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