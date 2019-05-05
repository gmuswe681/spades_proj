package com.spades.spades.resources;

import com.spades.spades.model.Games;
import com.spades.spades.repository.GamesRepository;
import com.spades.spades.service.GetCurrentPlayerInfoService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("/")
@RestController
public class HelloResource {

    private static final Logger LOGGER = LogManager.getLogger("HelloResource.class");


    @Autowired
    private GetCurrentPlayerInfoService currentPlayerInfoService;
    private final GamesRepository gamesRepository;
    HelloResource(GamesRepository g)
    {
        gamesRepository = g;
    }

    @GetMapping("/")
    public String hello() {
      String result = "<!DOCTYPE html>\n";
        result += "<html>\n";
        result += "<head><meta charset=\"UTF-8\"/></head>\n";
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
    public String securedHello(HttpServletRequest req)
    {
        int playerID1 = currentPlayerInfoService.findPlayerId();
        String buttonOrLink = "";
        CsrfToken token = (CsrfToken) req.getAttribute("_csrf");
        if(playerID1 >= 0) {
            LOGGER.info(currentPlayerInfoService.findPlayerName() + 
                " has accessed the application " + System.currentTimeMillis());
            if (getUserOpenGames(playerID1) != -1) {
                int gameId = getUserOpenGames(playerID1);
                buttonOrLink = "<a href=\"/secured/all/game/" + gameId + "\">Go to Existing Open Game</a>\n";
            } else {
                buttonOrLink = "<form><button type=\"submit\" formmethod=\"post\" formaction=\"/secured/all/creategame\">Create Game</button>";
                buttonOrLink += "<input type=\"hidden\" name=\"" + token.getParameterName() + "\" value=\"" + token.getToken() + "\"/>";
                buttonOrLink += "</form>";
                buttonOrLink += "<a href=\"/secured/all/retrieveopengames/\">Find Games</a><br/>";
                buttonOrLink += "<a href=\"/secured/all/viewendedgames\">View Past Games</a><br/>\n";
                buttonOrLink += "<a href=\"/secured/all/viewwinlossstats\">View User Statistics</a><br/>\n";
            }
        }

        String result = "<!DOCTYPE html>\n<html>\n";
        result += "<head><meta charset=\"UTF-8\"/> </head>\n";
        result += "<body>\n";
        result += "<p>Hello! What Spades related thing would you like to do?</p>\n";

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
                LOGGER.debug("No open Games.");
                return -1;
            }
            return openGames.get(0).getGameId();
        }
}