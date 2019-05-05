package com.spades.spades.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.spades.spades.model.Games;
import com.spades.spades.model.Moves;
import com.spades.spades.model.Rounds;
import com.spades.spades.model.Users;
import com.spades.spades.repository.GamesRepository;
import com.spades.spades.repository.MovesRepository;
import com.spades.spades.repository.RoundsRepository;
import com.spades.spades.repository.UsersRepository;
import com.spades.spades.service.SpadesGameService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/secured/all/viewendedgames")
@RestController
public class ViewEndedGamesController {

    @Autowired
    private SpadesGameService spadesService;

    private final GamesRepository gamesRepository;
    private final RoundsRepository roundsRepository;
    private final UsersRepository usersRepository;
    private final MovesRepository movesRepository;

    ViewEndedGamesController(GamesRepository g, RoundsRepository r, UsersRepository u, MovesRepository m)
    {
        gamesRepository = g;
        roundsRepository = r;
        usersRepository = u;
        movesRepository = m;
    }

    // Allows any user to view the results of games that have ended.
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String findEndedGames()
    {
        StringBuilder result = new StringBuilder();
        result.append("<!DOCTYPE html>\n<html lang=\"en\">\n");
        result.append("<head><meta charset=\"UTF-8\"/></head>\n");
        result.append("<body>\n");
        result.append("<h1>List of Ended Games</h1>");

        result.append(generateEndedGameLinks());

        result.append("<a href=\"/secured/all\">Go Back</a>\n");
        result.append("<a href=\"/logout\">Logout</a>\n");
        result.append("</body>\n");
        result.append("</html>");
        return result.toString();
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/{gameid}", method = RequestMethod.GET)
    public String retrieveSpecificGame(@PathVariable int gameid)
    {
        StringBuilder result = new StringBuilder();
        result.append("<!DOCTYPE html>\n<html lang=\"en\">\n");
        result.append("<head><meta charset=\"UTF-8\"/></head>\n");
        result.append("<body>\n");

        Optional<Games> game = gamesRepository.findByGameId(gameid);
        if(game.isPresent())
        {
            Games g = game.get();
            if(g.getGameStatus().equals("e"))
            {
                result.append(renderSpecificGame(g));
            }
        }

        result.append("<a href=\"/secured/all/viewendedgames\">View More Games</a><br/>");
        result.append("<a href=\"/secured/all/\">Go Back</a><br/>\n");
        result.append("<a href=\"/logout\">Logout</a><br/>\n");
        result.append("</body>\n");
        result.append("</html>");
        return result.toString();
    }

    // Returns a list of links that can be used to access detailed information about specific games.
    private String generateEndedGameLinks()
    {
        List<Games> openGamesList =  gamesRepository.findByGameStatus("e");

        StringBuilder listResponse = new StringBuilder();
        if(openGamesList.size() > 0)
        {
            for (Games g : openGamesList) {
                int idGame = g.getGameId();
                String specificGameUrl = "/secured/all/viewendedgames/" + idGame;

                listResponse.append("<a href=\"" + specificGameUrl + "\">View Spades Game ID # " + idGame + "</a><br/>");
            }
        }
        listResponse.append("<br/>");
        return listResponse.toString();
    }

    // Creates HTML output to display information about a game.
    private String renderSpecificGame(Games g)
    {
        // Return nothing if this game doesn't have 2 players...
        if(g.getPlayer1Id() == null || g.getPlayer2Id() == null || g.getWinnerId() == null)
        {
            return "";
        }

        // Gets player information
        ArrayList<Users> players = new ArrayList<Users>();
        Optional<Users> player1 = usersRepository.findById(g.getPlayer1Id());
        Optional<Users> player2 = usersRepository.findById(g.getPlayer2Id());
        if((!player1.isPresent()) || (!player2.isPresent()))
        {
            return "";
        }
        players.add(player1.get());
        players.add(player2.get());

        // Displays players (and who won)
        String spadeGameName = "Spades Game #" + g.getGameId();
        StringBuilder result = new StringBuilder();
        result.append("<h1>" + spadeGameName + "</h1>");
        for(int i = 0; i < players.size(); i++)
        {
            Users u = players.get(i);
            result.append("<p>Player #" + (i+1) + ": " + u.getName());
            if(g.getWinnerId() == u.getId())
            {
                result.append("(WINNER)");
            }
            result.append("</p>");
        }

        // Gets round information
        List<Rounds> rounds = roundsRepository.findByGameIdOrderByRoundNumberAsc(g.getGameId());

        //Gets the rendering of the game table.
        String tableResult = spadesService.renderCompletedRounds(players, rounds);

        // Holds accumulated game data
        result.append(tableResult);

        // Retrieve all game moves from the moves table.
        List<Moves> moves = movesRepository.findByGameIdOrderByMoveIdAsc(g.getGameId());
        result.append("<h2>Move List</h2>");
        int moveCount = 1;
        for(Moves m : moves)
        {
            // Check which player made the move
            int playerId = m.getUserId();
            String playerName = "Unknown";
            for(Users u : players)
            {
                if(playerId == u.getId())
                {
                    playerName = u.getName();
                }
            }

            // Add the move.
            result.append("<p> Move " + moveCount + ": ");
            result.append(playerName + " played card " + m.getCardPlayed());
            result.append("</p>\n");
            moveCount++;
        }

        return result.toString();
    }
}