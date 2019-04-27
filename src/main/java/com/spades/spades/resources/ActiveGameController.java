package com.spades.spades.resources;

import com.spades.spades.GameTimeOut;
import com.spades.spades.model.Games;
import com.spades.spades.model.Rounds;
import com.spades.spades.repository.GamesRepository;
import com.spades.spades.service.GameTimerService;
import com.spades.spades.service.GetCurrentPlayerInfoService;
import com.spades.spades.service.SpadesGameService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestMapping("/secured/all/game")
@RestController
public class ActiveGameController {

    @Autowired
    private GetCurrentPlayerInfoService currentPlayerInfoService;

    @Autowired
    private SpadesGameService spadesService;

    @Autowired
    private GameTimerService gameTimerService;

    private final GamesRepository gamesRepository;

    private static final Logger LOGGER = LogManager.getLogger("ActiveGameController.class");

    private static GameTimeOut timer;

    ActiveGameController(GamesRepository g)
    {
        gamesRepository = g;

    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/{gameid}")
    public String updateGame(@PathVariable int gameid)
    {
        Optional<Games> foundGame = gamesRepository.findByGameId(gameid);

        timer = gameTimerService.getTimer(gameid);
        if(timer == null)
        {
            timer = new GameTimeOut(30000L);
        }
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

            int playerId = currentPlayerInfoService.findPlayerId();
            if(playerId == currGame.getPlayer1Id() || playerId == currGame.getPlayer2Id())
            {
                String response = spadesService.progressGame(gameid);
                timer.cancelTimeout();

                Rounds currRound = spadesService.getCurrentRoundStatus(gameid);
                if(currRound.getRoundStatus().equals("b"))
                {
                    response += "<form>";
                    response += "Enter your bid.";
                    response += "<input id=\"bidAmount\" name=\"bidAmount\" type=\"number\"></input>";
                    response += "<button type=\"submit\" formmethod=\"post\" formaction=\"/secured/all/game/" + currGame.getGameId() + "/submitBid\">Submit Bid</button>";
                    response += "</form>";
                }
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

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/{gameid}/submitBid", method = RequestMethod.POST)
    public void updateBid(HttpServletRequest req, HttpServletResponse resp, @PathVariable int gameid)
        throws IOException
    {
        Optional<Games> foundGame = gamesRepository.findByGameId(gameid);

        if(foundGame.isPresent())
        {
            Games currGame = foundGame.get();

            int playerId = currentPlayerInfoService.findPlayerId();
            if(playerId == currGame.getPlayer1Id() || playerId == currGame.getPlayer2Id())
            {
                int bidAmount = -1;
                try
                {
                    bidAmount = Integer.parseInt(req.getParameter("bidAmount"));
                }
                catch (NumberFormatException e)
                {
                    LOGGER.debug("User passed input that wasn't a number.");
                }

                Rounds currRound = spadesService.getCurrentRoundStatus(gameid);
                if((bidAmount > 0) && (bidAmount <= 13) && (currRound.getRoundStatus().equals("b")))
                {
                    spadesService.submitBid(gameid, bidAmount);
                }
            }
        }

        String gameURL = "/secured/all/game/" + gameid;
        resp.sendRedirect(gameURL);
    }

    private String getResponse(String logResponse, String htmlResponse) {
        LOGGER.debug(logResponse);
        return generateHtmlResponse(htmlResponse);
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