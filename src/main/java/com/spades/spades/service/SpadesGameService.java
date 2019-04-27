/****
 * Provides interaction to allow users to play a Spades Game.
 * Processes input from users.
 ****/

package com.spades.spades.service;

import com.spades.spades.Hand;
import com.spades.spades.SpadesRoundImpl;
import com.spades.spades.model.Games;
import com.spades.spades.repository.GamesRepository;

import com.spades.spades.model.Rounds;
import com.spades.spades.repository.RoundsRepository;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class SpadesGameService {

    private Map<Integer, SpadesRoundImpl> spadeGamesStorage;

    @Autowired
    private GamesRepository gamesRepository;

    @Autowired
    private RoundsRepository roundsRepository;

    @Autowired
    private GetCurrentPlayerInfoService currentPlayerInfoService;


    public SpadesGameService()
    {
        spadeGamesStorage = new HashMap<Integer, SpadesRoundImpl>();
    }

    /****
     * A method to render the current state of the game.
     * Will progress the game as needed.
     ****/
    public String progressGame(int gameId)
    {
        Optional<Games> foundGame = gamesRepository.findByGameId(gameId);
        if(foundGame.isPresent())
        {
            int playerId = currentPlayerInfoService.findPlayerId();
            if(playerId >= 0)
            {
                Rounds round = createOrGetRound(gameId, playerId);
                if(round == null)
                {
                    return "Game: something went wrong";
                }

                String roundRender = renderRound(round, playerId);

                if(roundRender == "")
                {
                    return "Game: something went wrong";
                }
                return roundRender;
            }
            else
            {
                return "Game: something went wrong";
            }
        }
        else
        {
            return "Game not found";
        }
    }

    /****
     * Based on the current game status, will either create a new round or
     * return the current round.
     ****/
    private Rounds createOrGetRound(int gameId, int playerId)
    {
        // Checks that the given game exists
        Optional<Games> foundGame = gamesRepository.findByGameId(gameId);
        if(foundGame.isPresent())
        {
            Games g = foundGame.get();

            // Checks that the game is still active, and that the player has access to it.
            if( (g.getGameStatus().equals("a")) &&
                ((playerId == g.getPlayer1Id()) || playerId == g.getPlayer2Id()) )
            {
                // Checks for an existing round, and returns if if it does
                Optional<Rounds> currRound = roundsRepository.findByGameIdAndRoundStatusNot(gameId, "e");
                if(currRound.isPresent())
                {
                    return currRound.get();
                }

                // No round is in progress, so find out the next round number
                List<Rounds> roundsList = roundsRepository.findByGameId(gameId);
                int roundnum = 1;
                if(roundsList.size() > 0)
                {
                    for(Rounds temp : roundsList)
                    {
                        if(temp.getRoundNumber() > roundnum)
                        {
                            roundnum = temp.getRoundNumber();
                        }
                    }
                }

                // Creates the new Round
                Rounds newRound = insertRoundIntoDatabase(g, roundnum);
                spadeGamesStorage.put(new Integer(gameId), new SpadesRoundImpl());
                return newRound;
            }
        }

        return null;
    }


    /****
     * Inserts a new round into the database using the current game.
     ****/
    private Rounds insertRoundIntoDatabase(Games game, int roundNum)
    {
        if(game != null)
        {
            Rounds newRound = new Rounds();
            newRound.setGameId(game.getGameId());
            newRound.setRoundNumber(roundNum);
            newRound.setPlayer1Id(game.getPlayer1Id());
            newRound.setPlayer1Bid(-1);
            newRound.setPlayer2Id(game.getPlayer2Id());
            newRound.setPlayer2Bid(-1);
            newRound.setRoundStatus("b"); // People are bidding.
            return roundsRepository.save(newRound);
        }

        return null;
    }

    private String renderRound(Rounds round, int playerId)
    {
        // Checks for player access to this game.
        if(round == null)
        {
            return "";
        }
        if((playerId != round.getPlayer1Id()) && (playerId != round.getPlayer2Id()))
        {
            return "";
        }

        // Gets the current game state from software
        if(!spadeGamesStorage.containsKey(round.getGameId()))
        {
            //TODO: reconstruct a working game state?
            spadeGamesStorage.put(new Integer(round.getGameId()), new SpadesRoundImpl());
        }
        SpadesRoundImpl sGame = spadeGamesStorage.get(round.getGameId());

        // Displays the current round.
        String result = "<h1>Round # " + round.getRoundNumber() + "</h1>\n";

        // Display which player the user is.
        if(playerId == round.getPlayer1Id())
        {
            result += "<p>You are Player 1.</p>";
        }
        else if(playerId == round.getPlayer2Id())
        {
            result += "<p>You are Player 2.</p>";
        }

        // Displays the hand for each player.
        result += "<p>Your hand:</p>\n";
        if(playerId == round.getPlayer1Id())
        {
            result += "<p>";
            Hand p1Hand = sGame.getHand1();
            for(String s : p1Hand.getHand())
            {
                result += s + " ";
            }
            result += "</p>\n";
        }
        else if(playerId == round.getPlayer2Id())
        {
            result += "<p>";
            Hand p2Hand = sGame.getHand2();
            for(String s : p2Hand.getHand())
            {
                result += s + " ";
            }
            result += "</p>\n";
        }

        // Displays the scoring for each player.
        if(round.getRoundStatus().equals("a"))
        {
            result += "<p>Player 1's Bid: " + round.getPlayer1Bid() + "</p>";
            result += "<p>Player 2's Bid: " + round.getPlayer2Bid() + "</p>";
        }
        else if(round.getRoundStatus().equals("b"))
        {
            if((playerId == round.getPlayer1Id()) && (round.getPlayer1Bid() > 0))
            {
                result += "<p>Your current Bid: " + round.getPlayer1Bid() + "</p>";
            }
            else if(playerId == round.getPlayer2Id() && (round.getPlayer2Bid() > 0))
            {
                result += "<p>Your current Bid: " + round.getPlayer2Bid() + "</p>";
            }
        }

        return result;
    }

    public Rounds getCurrentRoundStatus(int gameId)
    {
        Optional<Games> foundGame = gamesRepository.findByGameId(gameId);
        if(foundGame.isPresent())
        {
            int playerId = currentPlayerInfoService.findPlayerId();
            if(playerId >= 0)
            {
                Optional<Rounds> currRound = roundsRepository.findByGameIdAndRoundStatusNot(gameId, "e");
                if(currRound.isPresent())
                {
                    return currRound.get();
                }
            }
            else
            {
                return null;
            }
        }
        
        return null;
    }

    // Accepts bids from players.
    public void submitBid(int gameId, int amount)
    {
        // Gets player and round information.
        Rounds r = getCurrentRoundStatus(gameId);
        int playerId = currentPlayerInfoService.findPlayerId();

        // Checks that the round is valid and is currently in *bidding* status
        if((r == null) || (!r.getRoundStatus().equals("b")))
        {
            return;
        }

        // Checks that the player has access to this round.
        if((r.getPlayer1Id() != playerId) && (r.getPlayer2Id() != playerId))
        {
            return;
        }

        // Checks that the bid amount is valid.
        if(amount < 0 || amount > 13)
        {
            return;
        }

        // Updates player's bids. Checks for a negative value (the default)
        // before updating, to prevent players 
        if((r.getPlayer1Id() == playerId) && (r.getPlayer1Bid() < 0))
        {
            r.setPlayer1Bid(amount);
            r = roundsRepository.save(r);
        }
        else if((r.getPlayer2Id() == playerId) && (r.getPlayer2Bid() < 0))
        {
            r.setPlayer2Bid(amount);
            r = roundsRepository.save(r);
        }

        // If both player's bids are accepted, then change the round status to active.
        if((r.getPlayer1Bid() >= 0) && (r.getPlayer2Bid() >= 0))
        {
            r.setRoundStatus("a");
            roundsRepository.save(r);
        }
    }
}

