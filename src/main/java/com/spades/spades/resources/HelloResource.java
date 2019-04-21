package com.spades.spades.resources;


import com.spades.spades.model.Games;
import com.spades.spades.repository.GamesRepository;
import com.spades.spades.service.GetCurrentPlayerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/")
@RestController
public class HelloResource {


    @Autowired
    private GetCurrentPlayerInfoService currentPlayerInfoService;
    private final GamesRepository gamesRepository;
    HelloResource(GamesRepository g)
    {
        gamesRepository = g;
    }

    @GetMapping("/")
    public String hello() {
      String result = "<html>\n";
        result += "<head></head>\n";
        result += "<body>\n";
        result += "<p>Welcome to Spades. Please Create a User or Login.</p>\n";
        result += "<a href=\"/secured/all\">Login</a>\n";
        result += "<a href=\"/createuserpage.html\">Create User</a>\n";
        result += "</body>\n";
        result += "</html>";

        return result;
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/secured/all")
    public String securedHello()
    {
        int playerID1 = currentPlayerInfoService.findPlayerId();
        String buttonOrLink = "";
        if(playerID1 >= 0) {
            if (getUserOpenGames(playerID1) != -1) {
                int gameId = getUserOpenGames(playerID1);
                buttonOrLink = "<a href=\"/secured/all/game/" + gameId + "\">Go to Existing Open Game</a>\n";
            } else {
                buttonOrLink = "<form><button type=\"submit\" formmethod=\"post\" formaction=\"/secured/all/creategame\">Create Game</button></form>";
                buttonOrLink += "<form><button type=\"submit\" formmethod=\"get\" formaction=\"/secured/all/retrieveopengames/\">Find Games</button></form>";
            }
        }

        String result = "<html>\n";
        result += "<head></head>\n";
        result += "<body>\n";
        result += "<p>Secured Hello!</p>\n";

        result += buttonOrLink;

        result += "<a href=\"/logout\">Logout</a>\n";
        result += "</body>\n";
        result += "</html>";
        return result;
    }

        private Integer getUserOpenGames(int userId)
        {
            List<Games> openGames = gamesRepository.findOpenGamesForUser(userId);
            if(openGames.size() == 0)
            {
                return -1;
            }
            return openGames.get(0).getGameId();
        }
}