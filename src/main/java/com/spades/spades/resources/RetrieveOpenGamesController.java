package com.spades.spades.resources;

import java.util.List;

import com.spades.spades.model.Games;
import com.spades.spades.repository.GamesRepository;
import com.spades.spades.service.GetCurrentPlayerInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/secured/all/retrieveopengames")
@RestController
public class RetrieveOpenGamesController {

    @Autowired
    private GetCurrentPlayerInfoService currentPlayerInfoService;

    private final GamesRepository gamesRepository;

    RetrieveOpenGamesController(GamesRepository g)
    {
        gamesRepository = g;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String retrieveOpenGames()
    {
        String result = "<!DOCTYPE html>\n";
        result += "<htmllang=\"en\">\n";
        result += "<head><meta charset=\"UTF-8\"/></head>\n";
        result += "<body>\n";
        result += "<h1>Open Games</h1>";

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

        StringBuilder listResponse = new StringBuilder();
        if(openGamesList.size() > 0)
        {
            int playerId = currentPlayerInfoService.findPlayerId();
            for (Games g : openGamesList) {
                if(g.getPlayer1Id() == playerId)
                {
                    continue;
                }

                listResponse.append("<form>\n");
                listResponse.append("Spades Game ID#" + g.getGameId());
                listResponse.append("<input type=\"hidden\" name=\"game_id\" value=\"" + g.getGameId() + "\"/>\n");
                listResponse.append("<button type=\"submit\" formmethod=\"post\" formaction=\"/secured/all/joingame\">Join Game</button>");
                listResponse.append("</form>\n");
            }
        }
        return listResponse.toString();
    }
}