package com.spades.spades.resources;

import com.spades.spades.GameTimeOut;
import com.spades.spades.TimerInterface;
import com.spades.spades.model.Games;
import com.spades.spades.model.Users;
import com.spades.spades.repository.GamesRepository;
import com.spades.spades.repository.UsersRepository;
import com.spades.spades.service.GenerateGameIdService;
import com.spades.spades.service.GetAuthenticationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.Timer;

@RequestMapping("/secured/all/creategame")
@RestController
public class CreateGameController implements TimerInterface{

    @Autowired
    private GetAuthenticationService authService;

    @Autowired
    private GenerateGameIdService gameIdService;


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

    private int createGameInDatabase(int playerID)
    {
        Games g = new Games();
        int newId = gameIdService.getNewGameId();

        g.setGameId(newId);
        g.setPlayer1Id(playerID);
        g.setGameStatus("o");
        g.setPointsToWin(100);
        gamesRepository.save(g);

        LOGGER.info("game created with id =" + newId);

        //Start timer to wait on 2nd player
        Timer timer = new Timer("GameTimeout");
        onStartTimer(timer);
        return newId;
    }

    private boolean checkUserNotInOpenGames(int userId)
    {
        List<Games> openGames = gamesRepository.findOpenGamesForUser(userId);
        if(openGames.size() == 0)
        {
            return true;
        }
        return false;
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

    @Override
    public void onStartTimer(Timer timer) {
        GameTimeOut gameTimer = new GameTimeOut(5000L);
        gameTimer.registerTimer(timer);
    }
}