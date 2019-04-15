package com.spades.spades.resources;

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

import javax.servlet.http.HttpServletRequest;

import java.security.cert.PKIXRevocationChecker.Option;
import java.util.Optional;

@RequestMapping("/secured/all/joingame")
@RestController
public class JoinGameController {

    @Autowired
    private GetAuthenticationService authService;


    private final GamesRepository gamesRepository;
    private final UsersRepository usersRepository;

    private static final Logger LOGGER = LogManager.getLogger("JoinGameController.class");

    JoinGameController(GamesRepository g, UsersRepository u)
    {
        gamesRepository = g;
        usersRepository = u;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public String joinGame(HttpServletRequest req)
    {
        int gameId = Integer.parseInt(req.getParameter("game_id"));
        int playerID2 = findPlayerID();
        if(playerID2 >= 0)
        {
            joinGameInDatabase(gameId, playerID2);
            return generateHtmlResponse("game was joined");
        }
        else
        {
            return generateHtmlResponse("Unable to create game.");
        }
    }

    private void joinGameInDatabase(int gameId, int playerId)
    {
        Optional<Games> foundGame = gamesRepository.findByGameId(gameId);
        if(foundGame.isPresent())
        {
            Games g = foundGame.get();
            if(g.getPlayer2Id() == null)
            {
                g.setPlayer2Id(playerId);
                g.setGameStatus("a");
                gamesRepository.save(g);
                LOGGER.info("Joined game id = " + gameId);
            }
            else
            {
                LOGGER.error("Game is full");
            }
        }
        else
        {
            LOGGER.error("Attempted to join non-existent game");
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

        result += "<a href=\"/secured/all\">Go Back</a>\n";
        result += "</body>\n";
        result += "</html>";
        return result;
    }
}