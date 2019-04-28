package com.spades.spades.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.spades.spades.model.Games;
import com.spades.spades.model.Users;
import com.spades.spades.repository.GamesRepository;
import com.spades.spades.repository.UsersRepository;
import com.spades.spades.service.GetCurrentPlayerInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/secured/all/viewwinlossstats")
@RestController
public class ViewWinLossStatsController {

    @Autowired
    private GetCurrentPlayerInfoService currentPlayerInfoService;

    private final GamesRepository gamesRepository;
    private final UsersRepository usersRepository;

    ViewWinLossStatsController(GamesRepository g, UsersRepository u)
    {
        gamesRepository = g;
        usersRepository = u;
    }

    // Allows any user to view the results of games that have ended.
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @RequestMapping(value = "")
    public String viewStatistics()
    {
        String result = "<html>\n";
        result += "<head></head>\n";
        result += "<body>\n";

        result += generateStatisticsLinks();

        result += "<a href=\"/secured/all\">Go Back</a>\n";
        result += "<a href=\"/logout\">Logout</a>\n";
        result += "</body>\n";
        result += "</html>";
        return result;
    }

    // Renders statistics for all users.
    private String generateStatisticsLinks()
    {
        List<Users> users = usersRepository.findAll();

        String personalResponse = ""; // To hold statistics specific to this user.
        String othersResponse = "<h1>Other User's Statistics</h1>"; // To hold statistics for all users.

        // Iterates through all users.
        for (Users u : users) {
            List<Games> games = gamesRepository.findEndedGamesForUser(u.getId());

            // Gets statistics for user by counting games won/lost
            int wins = 0;
            int losses = 0;
            for(Games g : games)
            {
                if(g.getWinnerId() == u.getId())
                {
                    wins += 1;
                }
                else
                {
                    losses += 1;
                }
            }

            if(currentPlayerInfoService.findPlayerId() == u.getId())
            {
                personalResponse = "<h1>Statistics for " + u.getName() + "</h1>\n";
                personalResponse += "<p> Win/Loss: " + wins + "/" + losses + "</p>";
            }
            else
            {
                othersResponse += "<p>Statistics for " + u.getName() + " (win/loss): " + wins + "/" + losses + "</p>";
            }
        }
        
        String result = personalResponse + othersResponse;
        return result;
    }
}