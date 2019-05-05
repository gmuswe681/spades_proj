package com.spades.spades.resources;

import com.spades.spades.GameTimeOut;
import com.spades.spades.model.Games;
import com.spades.spades.repository.GamesRepository;
import com.spades.spades.service.GameTimerService;
import com.spades.spades.service.GenerateGameIdService;
import com.spades.spades.service.GetCurrentPlayerInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@RequestMapping("/secured/all/creategame")
@RestController
public class CreateGameController {

    @Autowired
    private GetCurrentPlayerInfoService currentPlayerInfoService;

    @Autowired
    private GenerateGameIdService gameIdService;

    @Autowired
    private GameTimerService gameTimerService;

    private final GamesRepository gamesRepository;


    private static final Logger LOGGER = LogManager.getLogger("CreateGameController.class");

    CreateGameController(GamesRepository g)
    {
        gamesRepository = g;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public String createGame()
    {
        int playerID1 = currentPlayerInfoService.findPlayerId();
        String response = "";

        if(playerID1 >= 0)
        {
            if(checkUserNotInOpenGames(playerID1))
            {
                int gameId = createGameInDatabase(playerID1);
                response = "<p>Game was created.</p>\n";
                response += "<a href=\"/secured/all/game/" + gameId + "\">Go to new game</a>\n";
            }
            else
            {
                response = "<p>Finish your current game first.</p>\n";
                response += "<a href=\"/secured/all\">Go Back</a>\n";
            }
        }
        else
        {
            response = "<p>Unable to create game.</p><br/>\n";
            response += "<a href=\"/secured/all\">Go Back</a>\n";
        }

        return generateHtmlResponse(response);
    }

    private int createGameInDatabase(int playerID)
    {
        Games g = new Games();
        int newId = gameIdService.getNewGameId();

        g.setGameId(newId);
        g.setPlayer1Id(playerID);
        g.setGameStatus("o");
        g.setPointsToWin(200);
        gamesRepository.save(g);

        LOGGER.info("game created with id =" + newId);

        //Start timer to wait on 2nd player set at 5 minutes
        GameTimeOut gameTimeOut = new GameTimeOut(300000L);
        gameTimerService.setTimer(newId, gameTimeOut);
        return newId;
    }

    private boolean checkUserNotInOpenGames(int userId)
    {
        List<Games> openGames = gamesRepository.findOpenGamesForUser(userId);
        return openGames.size() == 0;
    }

    private String generateHtmlResponse(String s)
    {
        String result = "<!DOCTYPE html>\n";
        result += "<html lang=\"en\">\n";
        result += "<head><meta charset=\"UTF-8\"/></head>\n";
        result += "<body>\n";

        result += s + "\n";

        result += "</body>\n";
        result += "</html>";
        return result;
    }


}