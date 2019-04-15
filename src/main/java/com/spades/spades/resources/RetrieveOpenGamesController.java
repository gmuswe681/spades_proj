package com.spades.spades.resources;

import java.util.List;

import com.spades.spades.model.Games;
import com.spades.spades.repository.GamesRepository;
import com.spades.spades.repository.UsersRepository;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/secured/all/retrieveopengames")
@RestController
public class RetrieveOpenGamesController {

    private final GamesRepository gamesRepository;
    private final UsersRepository usersRepository;

    RetrieveOpenGamesController(GamesRepository g, UsersRepository u)
    {
        gamesRepository = g;
        usersRepository = u;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String retrieveOpenGames()
    {
        String result = "<html>\n";
        result += "<head></head>\n";
        result += "<body>\n";

        result += generateOpenGames();

        result += "<a href=\"/secured/all\">Go Back</a>\n";
        result += "<a href=\"/logout\">Logout</a>\n";
        result += "</body>\n";
        result += "</html>";
        return result;
    }

    private String generateOpenGames()
    {
        List<Games> openGamesList =  gamesRepository.findByGameStatus("o");

        String listResponse = "";
        if(openGamesList.size() > 0)
        {
            for (Games g : openGamesList) {
                listResponse += "<form>\n";
                listResponse += "Spades Game ID#" + g.getGameId();
                listResponse += "<input type=\"hidden\" name=\"game_id\" value=\"" + g.getGameId() + "\"/>\n";
                listResponse += "<button type=\"submit\" formmethod=\"post\" formaction=\"/secured/all/joingame\">Join Game</button>";
                listResponse += "</form>\n";
            }
        }
        return listResponse;
    }
}