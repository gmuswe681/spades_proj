package com.spades.spades.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.spades.spades.model.Games;
import com.spades.spades.model.Rounds;
import com.spades.spades.model.Users;
import com.spades.spades.repository.GamesRepository;
import com.spades.spades.repository.RoundsRepository;
import com.spades.spades.repository.UsersRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/secured/all/viewendedgames")
@RestController
public class ViewEndedGamesController {

    private final GamesRepository gamesRepository;
    private final RoundsRepository roundsRepository;
    private final UsersRepository usersRepository;

    ViewEndedGamesController(GamesRepository g, RoundsRepository r, UsersRepository u)
    {
        gamesRepository = g;
        roundsRepository = r;
        usersRepository = u;
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

        // Gets round information
        List<Rounds> rounds = roundsRepository.findByGameId(g.getGameId());

        String spadeGameName = "Spades Game" + g.getGameId();
        String result = "<h1>" + spadeGameName + "</h1>";
        for(Users u : players)
        {
            if(g.getWinnerId() == u.getId())
            {
                result += "<p>Winner of this game: " + u.getName() + "</p>";
            }
        }

        return result;
    }
}