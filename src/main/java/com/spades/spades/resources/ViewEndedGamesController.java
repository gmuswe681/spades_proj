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
        String result = "<html>\n";
        result += "<head></head>\n";
        result += "<body>\n";
        result += "<h1>List of Ended Games</h1>";

        result += generateEndedGameLinks();

        result += "<a href=\"/secured/all\">Go Back</a>\n";
        result += "<a href=\"/logout\">Logout</a>\n";
        result += "</body>\n";
        result += "</html>";
        return result;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "/{gameid}", method = RequestMethod.GET)
    public String retrieveSpecificGame(@PathVariable int gameid)
    {
        String result = "<html>\n";
        result += "<head></head>\n";
        result += "<body>\n";

        Optional<Games> game = gamesRepository.findByGameId(gameid);
        if(game.isPresent())
        {
            Games g = game.get();
            if(g.getGameStatus().equals("e"))
            {
                result += renderSpecificGame(g);
            }
        }

        result += "<a href=\"/secured/all/viewendedgames\">View More Games</a><br/>";
        result += "<a href=\"/secured/all/\">Go Back</a><br/>\n";
        result += "<a href=\"/logout\">Logout</a><br/>\n";
        result += "</body>\n";
        result += "</html>";
        return result;
    }

    // Returns a list of links that can be used to access detailed information about specific games.
    private String generateEndedGameLinks()
    {
        List<Games> openGamesList =  gamesRepository.findByGameStatus("e");

        String listResponse = "";
        if(openGamesList.size() > 0)
        {
            for (Games g : openGamesList) {
                int idGame = g.getGameId();
                String specificGameUrl = "/secured/all/viewendedgames/" + idGame;

                listResponse += "<a href=\"" + specificGameUrl + "\">View Spades Game ID # " + idGame + "</a><br/>";
            }
        }
        listResponse += "<br/>";
        return listResponse;
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
        String result = "<h1>" + spadeGameName + "</h1>";
        for(int i = 0; i < players.size(); i++)
        {
            Users u = players.get(i);
            result += "<p>Player #" + (i+1) + ": " + u.getName();
            if(g.getWinnerId() == u.getId())
            {
                result += "(WINNER)";
            }
            result += "</p>";
        }

        // Gets round information
        List<Rounds> rounds = roundsRepository.findByGameId(g.getGameId());

        // Creates table headers
        String tableResult = "<table border=\"1\">";
        tableResult += "<tr>";
        tableResult += "<th>Round</th>";
        for(int i = 0; i < players.size(); i++)
        {
            String playerString = players.get(i).getName();
            tableResult += "<th>" + playerString + "'s Bid</th>";
            tableResult += "<th>" + playerString + "'s Actual</th>";
            tableResult += "<th>" + playerString + "'s Score</th>";
            tableResult += "<th>" + playerString + "'s Bags</th>";
        }
        tableResult += "</tr>\n";

        // Holds accumulated game data
        ArrayList<Integer> totalBags = new ArrayList<Integer>(players.size());
        ArrayList<Integer> totalScore = new ArrayList<Integer>(players.size());
        for(int i = 0; i < players.size(); i++)
        {
            totalBags.add(0);
            totalScore.add(0);
        }

        for(Rounds r : rounds)
        {
            // Gets the round number
            tableResult += "<tr>";
            tableResult += "<td>" + r.getRoundNumber() + "</td>";

            // Creates a list containing round information for each player
            ArrayList<Integer> roundBid = new ArrayList<Integer>(players.size());
            ArrayList<Integer> roundActual = new ArrayList<Integer>(players.size());
            roundBid.add(r.getPlayer1Bid());
            roundBid.add(r.getPlayer2Bid());
            roundActual.add(r.getPlayer1Actual());
            roundActual.add(r.getPlayer2Actual());
            
            // Processes data for each player
            for(int i = 0; i < players.size(); i++)
            {
                int points = spadesService.calculatePoints(roundBid.get(i), roundActual.get(i));
                int bags = spadesService.calculateBags(roundBid.get(i), roundActual.get(i));

                // Updates accumulated data for a player.
                int accumulatedPoints = totalScore.get(i);
                totalScore.set(i, accumulatedPoints + points);
                int accumulatedBags = totalBags.get(i);
                totalBags.set(i, accumulatedBags + bags);

                // Adds to table display
                tableResult += "<td>" + roundBid.get(i) + "</td>";
                tableResult += "<td>" + roundActual.get(i) + "</td>";
                tableResult += "<td>" + points + "</td>";
                tableResult += "<td>" + bags + "</td>";
            }

            tableResult += "</tr>\n";
        }
        tableResult += "</table>";

        result += tableResult;

        // Now prints the aggregate results for each player.
        for(int i = 0; i < players.size(); i++)
        {
            int rawPoints = totalScore.get(i);
            int numBags = totalBags.get(i);
            int finalPoints =  rawPoints - ((numBags / 10) * 100);

            String playerString = players.get(i).getName();
            result += "<p>" + playerString + " Raw Score: " + rawPoints + "<br/>\n";
            result += playerString + " Total Bags: " + numBags + "<br/>\n";
            result += playerString + " Adjusted Score: " + finalPoints + "<br/>\n";
            result += "</p>\n";
        }


        // Retrieve all game moves from the moves table.
        List<Moves> moves = movesRepository.findByGameIdOrderByMoveIdAsc(g.getGameId());
        result += "<h2>Move List</h2>";
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
            result += "<p> Move " + moveCount + ": ";
            result += playerName + " played card " + m.getCardPlayed();
            result += "</p>\n";
            moveCount++;
        }

        return result;
    }
}