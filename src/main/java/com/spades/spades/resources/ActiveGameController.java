/****
 * The endpoint exposed to interact with open Spades games.
 ****/

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private static final Pattern VALID_CARD_REGEX =
    Pattern.compile("^([2-9JQKA]|10)[CDHS]$", Pattern.CASE_INSENSITIVE);

    ActiveGameController(GamesRepository g)
    {
        gamesRepository = g;

    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/{gameid}")
    public String updateGame(@PathVariable int gameid, HttpServletResponse resp)
    {
        // Checks that the game being accessed is valid.
        Optional<Games> foundGame = gamesRepository.findByGameId(gameid);

        GameTimeOut timer = gameTimerService.getTimer(gameid);
        if(timer == null)
        {
            timer = new GameTimeOut(30000L);
        }
        if(foundGame.isPresent())
        {
            Games currGame = foundGame.get();

            // Just prints a waiting screen if players haven't joined yet.
            if(currGame.getGameStatus().equals("o"))
            {
                if(timer.getMessage().equals("running")){
                    return getResponse("This game is waiting for players.", "Waiting for a player to join. Refresh the page.");
                } else {
                   return getResponse(timer.getMessage(), timer.getMessage());
                }

            }

            // Redirects if this game has ended.
            if(currGame.getGameStatus().equals("e"))
            {
                try
                {
                    String gameURL = "/secured/all/viewendedgames/" + gameid;
                    resp.sendRedirect(gameURL);
                }
                catch (IOException e)
                {
                    String errResponse = "<p>Sorry, an error occurred on our end.</p>\n";
                    errResponse += "<a href=\"/\">Go to homepage</a>";
                    return getResponse("Unable to redirect to ended game", errResponse);
                }
            }

            if(!currGame.getGameStatus().equals("a"))
            {
                if(timer.getMessage().equals("running")) {
                    return getResponse("This game is not active.", "Invalid");
                } else {
                    return getResponse(timer.getMessage(), timer.getMessage());
                }
            }

            int playerId = currentPlayerInfoService.findPlayerId();
            if(playerId == currGame.getPlayer1Id() || playerId == currGame.getPlayer2Id())
            {
                timer.cancelTimeout();

                // Renders the current state of the game
                String response = spadesService.progressGame(gameid);

                // Renders input fields based on the current state of the game.
                Rounds currRound = spadesService.getCurrentRoundStatus(gameid);
                if(currRound.getRoundStatus().equals("b"))
                {
                    // Renders HTML to submit bidding amounts.
                    response += "<form>";
                    response += "Enter your bid: ";
                    response += "<input id=\"bidAmount\" name=\"bidAmount\" type=\"number\" maxlength=\"2\"></input>";
                    response += "<button type=\"submit\" formmethod=\"post\" formaction=\"/secured/all/game/" + currGame.getGameId() + "/submitBid\">Submit Bid</button>";
                    response += "</form>";
                }
                else if(currRound.getRoundStatus().equals("a"))
                {
                    // Renders HTML to submit cards.
                    response += "<form>";
                    response += "Enter a card to play: ";
                    response += "<input id=\"card\" name=\"card\" type=\"text\" maxlength=\"4\"></input>";
                    response += "<button type=\"submit\" formmethod=\"post\" formaction=\"/secured/all/game/" + currGame.getGameId() + "/submitCard\">Submit Card</button>";
                    response += "</form>";
                }

                return generateHtmlResponse(response);
            }
            else
            {
                if(timer.getMessage().equals("running")) {
                    return getResponse("This user is not part of this game.", "Invalid");
                } else {
                    return getResponse(timer.getMessage(), timer.getMessage());
                }

            }
        }
        else
        {
            if(timer.getMessage().equals("running")){
                return getResponse("Game ID not found", "Invalid");
            }   else {
                return getResponse(timer.getMessage(), timer.getMessage());
            }

        }
    }

    /****
     * Used to process bidding inputs from the user.
     ****/
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
                    LOGGER.error("User passed input that wasn't a number.");
                }

                Rounds currRound = spadesService.getCurrentRoundStatus(gameid);
                if((bidAmount >= 0) && (bidAmount <= 13) && (currRound.getRoundStatus().equals("b")))
                {
                    spadesService.submitBid(gameid, bidAmount);
                }
            }
        }

        String gameURL = "/secured/all/game/" + gameid;
        resp.sendRedirect(gameURL);
    }

    /****
     * Used to process card inputs from the user.
     ****/
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/{gameid}/submitCard", method = RequestMethod.POST)
    public void playCard(HttpServletRequest req, HttpServletResponse resp, @PathVariable int gameid)
        throws IOException
    {
        Optional<Games> foundGame = gamesRepository.findByGameId(gameid);

        if(foundGame.isPresent())
        {
            Games currGame = foundGame.get();

            int playerId = currentPlayerInfoService.findPlayerId();
            if(playerId == currGame.getPlayer1Id() || playerId == currGame.getPlayer2Id())
            {
                //Ensures that whatever the user entered it uppercases it
                String card = req.getParameter("card");

                if(card != null)
                {
                    card = card.toUpperCase();
                    Rounds currRound = spadesService.getCurrentRoundStatus(gameid);
                    Matcher matcher = VALID_CARD_REGEX.matcher(card);
                    if((matcher.find()) && (currRound.getRoundStatus().equals("a")))
                    {
                        spadesService.submitCard(gameid, card);
                    }
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
        result += "<script src=\"/reloadPage.js\"></script>";
        result += "</html>";
        return result;
    }
}