package com.spades.spades.resources;

import com.spades.spades.model.Games;
import com.spades.spades.repository.GamesRepository;
import com.spades.spades.service.GetCurrentPlayerInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

@RequestMapping("/secured/all/joingame")
@RestController
public class JoinGameController {

    @Autowired
    private GetCurrentPlayerInfoService currentPlayerInfoService;


    private final GamesRepository gamesRepository;

    private static final Logger LOGGER = LogManager.getLogger("JoinGameController.class");

    JoinGameController(GamesRepository g)
    {
        gamesRepository = g;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public void joinGame(HttpServletRequest req, HttpServletResponse resp)
        throws IOException
    {
        int gameId = Integer.parseInt(req.getParameter("game_id"));
        int playerID2 = currentPlayerInfoService.findPlayerId();
        if(playerID2 >= 0)
        {
            if(gamesRepository.findOpenGamesForUser(playerID2).size() != 0)
            {
                generateHtmlResponse(resp, "<p>Finish the games you are currently in first.</p>");
                return;
            }
            
            if(joinGameInDatabase(gameId, playerID2))
            {
                String gameURL = "/secured/all/game/" + gameId;
                resp.sendRedirect(gameURL);
            }
            else
            {
                generateHtmlResponse(resp, "<p>Something went wrong when trying to join the game.</p>");
            }
            return;
        }

        generateHtmlResponse(resp, "<p>Something went wrong when trying to join the game.</p>");
    }

    private boolean joinGameInDatabase(int gameId, int playerId)
    {
        Optional<Games> foundGame = gamesRepository.findByGameId(gameId);
        if(foundGame.isPresent())
        {
            Games g = foundGame.get();

            if(playerId == g.getPlayer1Id())
            {
                LOGGER.error("Player attempted to join own game.");
                return false;
            }

            if(g.getPlayer2Id() == null)
            {
                g.setPlayer2Id(playerId);
                g.setGameStatus("a");
                gamesRepository.save(g);
                LOGGER.debug("Joined game id = " + gameId);
                return true;
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

        return false;
    }

    private void generateHtmlResponse(HttpServletResponse resp, String s)
        throws IOException
    {
        String result = "<!DOCTYPE html>\n";
        result += "<html lang=\"en\">\n";
        result += "<head><meta charset=\"UTF-8\"/></head>\n";
        result += "<body>\n";

        result += s + "\n";

        result += "<a href=\"/secured/all\">Go Back</a>\n";
        result += "</body>\n";
        result += "</html>";
        
        PrintWriter out = resp.getWriter();
        out.println(result);
        out.flush();
    }
}