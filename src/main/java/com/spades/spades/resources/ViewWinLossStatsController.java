package com.spades.spades.resources;

import java.util.List;

import com.spades.spades.model.Games;
import com.spades.spades.model.Users;
import com.spades.spades.repository.GamesRepository;
import com.spades.spades.repository.UsersRepository;
import com.spades.spades.service.GetCurrentPlayerInfoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
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
        StringBuilder result = new StringBuilder();
        result.append("<html>\n");
        result.append("<head></head>\n");
        result.append("<body>\n");

        result.append(generateStatisticsLinks());

        result.append("<a href=\"/secured/all\">Go Back</a>\n");
        result.append("<a href=\"/logout\">Logout</a>\n");
        result.append("</body>\n");
        result.append("</html>");
        return result.toString();
    }

    // Renders statistics for all users.
    private String generateStatisticsLinks()
    {
        List<Users> users = usersRepository.findAll();

        StringBuilder personalResponse = new StringBuilder(); // To hold statistics specific to this user.
        StringBuilder othersResponse = new StringBuilder("<h1>Other User's Statistics</h1>"); // To hold statistics for all users.

        int currentPlayerId = currentPlayerInfoService.findPlayerId();

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

            if(currentPlayerId == u.getId())
            {
                personalResponse.append("<h1>Statistics for " + u.getName() + "</h1>\n");
                personalResponse.append("<p> Win/Loss: " + wins + "/" + losses + "</p>");
            }
            else
            {
                othersResponse.append("<p>Statistics for " + u.getName() + " (win/loss): " + wins + "/" + losses + "</p>");
            }
        }
        
        String result = personalResponse.toString() + othersResponse.toString();
        return result;
    }
}