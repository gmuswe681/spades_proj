/****
 * Provides interaction to allow users to play a Spades Game.
 * Processes input from users.
 ****/

package com.spades.spades.service;

import com.spades.spades.Hand;
import com.spades.spades.SpadesRoundImpl;
import com.spades.spades.model.Games;
import com.spades.spades.model.Moves;
import com.spades.spades.repository.GamesRepository;

import com.spades.spades.model.Rounds;
import com.spades.spades.repository.MovesRepository;
import com.spades.spades.repository.RoundsRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class SpadesGameService {

    private static final Logger LOGGER = LogManager.getLogger("SpadesGameService.class");

    private Map<Integer, SpadesRoundImpl> spadeGamesStorage;

    @Autowired
    private GamesRepository gamesRepository;

    @Autowired
    private RoundsRepository roundsRepository;

    @Autowired
    private GetCurrentPlayerInfoService currentPlayerInfoService;

    @Autowired
    private MovesRepository movesRepository;


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
                    LOGGER.error("Round is null.");
                    return "Game: something went wrong";
                }

                String roundRender = renderRound(round, playerId);

                if(roundRender == "")
                {
                    LOGGER.error("RoundRender is null.");
                    return "Game: something went wrong";
                }
                return roundRender;
            }
            else
            {
                LOGGER.error("Player not found.");
                return "Game: something went wrong";
            }
        }
        else
        {
            LOGGER.error("Game not found.");
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
                        if(temp.getRoundNumber() >= roundnum)
                        {
                            roundnum = temp.getRoundNumber() + 1;
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

        if(round.getRoundStatus().equals("a"))
        {
            // Displays the scoring for each player.
            result += "<p>Player 1's Bid: " + round.getPlayer1Bid() + "</p>";
            result += "<p>Player 1's Actual: " + round.getPlayer1Actual() + "</p>";
            result += "<p>Player 2's Bid: " + round.getPlayer2Bid() + "</p>";
            result += "<p>Player 2's Actual: " + round.getPlayer2Actual() + "</p>";
            result += "<br/>";

            // Display the current cards played.
            result += "<p>Player 1's Card: " + sGame.getPlayer1Card() + "</p>";
            result += "<p>Player 2's Card: " + sGame.getPlayer2Card() + "</p>";

            // Displays whose turn it currently is.
            result += "<p>It is Player " + sGame.getCurrentTurn() + "'s turn.</p>";
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

    /****
     * Accept bids from players
     ****/
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

    /****
     * Accepts cards from players
     ****/
    public void submitCard(int gameId, String card)
    {
        // Gets player and round information.
        Rounds r = getCurrentRoundStatus(gameId);
        int playerId = currentPlayerInfoService.findPlayerId();

        // Checks that the round is valid and is currently in *active* status
        if((r == null) || (!r.getRoundStatus().equals("a")))
        {
            return;
        }

        // Checks that the player has access to this round.
        if((r.getPlayer1Id() != playerId) && (r.getPlayer2Id() != playerId))
        {
            return;
        }

        // Gets the current state of the game
        SpadesRoundImpl sGame = spadeGamesStorage.get(gameId);
        if(sGame == null)
        {
            return;
        }

        int currentTurn = sGame.getCurrentTurn();
        boolean moveSuccess = false;
        // Checks for the player's turn.
        if((r.getPlayer1Id() == playerId) && (currentTurn == 1))
        {
            moveSuccess = sGame.playHand1(card);
        }
        else if((r.getPlayer2Id() == playerId) && (currentTurn == 2))
        {
            moveSuccess = sGame.playHand2(card);
        }
        else
        {
            // It is not the user's turn.
            return;
        }

        // If move successful, check if it is time to calculate a trick.
        if(moveSuccess)
        {
            if(!sGame.getPlayer1Card().equals("") && !sGame.getPlayer2Card().equals(""))
            {
                int playerWon = sGame.calculateTrick();
                if(playerWon == 1)
                {
                    int actual1 = r.getPlayer1Actual() + 1;
                    r.setPlayer1Actual(actual1);
                    r = roundsRepository.save(r);
                }
                else if(playerWon == 2)
                {
                    int actual2 = r.getPlayer2Actual() + 1;
                    r.setPlayer2Actual(actual2);
                    r = roundsRepository.save(r);
                }
            }
            Moves m = new Moves();
            m.setGameId(r.getGameId());
            m.setRoundNo(r.getRoundNumber());
            m.setUserId(playerId);
            m.setCardPlayed(card);
            movesRepository.save(m);

        }

        // If both players hands are empty, update the round status.
        if(sGame.getHand1().returnHandSize() == 0 && sGame.getHand2().returnHandSize() == 0)
        {
            r.setRoundStatus("e");
            roundsRepository.save(r);

            // Now check to see if the game has been won.
            checkWinCondition(gameId);
        }
    }

    private void checkWinCondition(int gameId)
    {
        Optional<Games> foundGame = gamesRepository.findByGameId(gameId);
        if(foundGame.isPresent())
        {
            Games g = foundGame.get();
            List<Rounds> roundsList = roundsRepository.findByGameId(gameId);

            if(roundsList.size() > 0)
            {
                int player1Points = 0;
                int player1Bags = 0;
                int player2Points = 0;
                int player2Bags = 0;

                //Calculates the number of points and bags accumulated over each round.
                for(Rounds temp : roundsList)
                {
                    int roundBid1 = temp.getPlayer1Bid();
                    int roundActual1 = temp.getPlayer1Actual();
                    
                    player1Points += calculatePoints(roundBid1, roundActual1);
                    player1Bags += calculateBags(roundBid1, roundActual1);

                    int roundBid2 = temp.getPlayer2Bid();
                    int roundActual2 = temp.getPlayer2Actual();
                    
                    player2Points += calculatePoints(roundBid2, roundActual2);
                    player2Bags += calculateBags(roundBid2, roundActual2);
                }

                //Subtracts from points based on the number of accumulated bags.
                player1Points -= (player1Bags / 10) * 100;
                player2Points -= (player2Bags / 10) * 100;

                // Checks who won
                int pointsToWin = g.getPointsToWin();
                if (player1Points > pointsToWin && player2Points > pointsToWin)
                {
                    // If both players reached the points to win mark, calculate
                    // who has more points
                    if(player1Points > player2Points)
                    {
                        g.setWinnerId(g.getPlayer1Id());
                        g.setGameStatus("e");
                        gamesRepository.save(g);
                    }
                    else if (player2Points > player1Points)
                    {
                        g.setWinnerId(g.getPlayer2Id());
                        g.setGameStatus("e");
                        gamesRepository.save(g);
                    }
                    else
                    {
                        // Players somehow managed to tie.
                        // Have to play another game.
                    }
                }
                else if(player1Points > pointsToWin)
                {
                    g.setWinnerId(g.getPlayer1Id());
                    g.setGameStatus("e");
                    gamesRepository.save(g);
                }
                else if(player2Points > pointsToWin)
                {
                    g.setWinnerId(g.getPlayer2Id());
                    g.setGameStatus("e");
                    gamesRepository.save(g);
                }
            }
        }
    }

    /****
     * Given a bid and the actual number of tricks, determines
     * how many points the player got
     ****/
    public int calculatePoints(int bid, int actual)
    {
        // The zero case.
        if(bid == 0)
        {
            if(actual == 0)
            {
                return 100;
            }
            else
            {
                return -100;
            }
        }
        else
        {
            if(actual >= bid)
            {
                return bid * 10 + (actual - bid);
            }
            else
            {
                return bid * -10;
            }
        }
    }

    /****
     * Given a bid and the actual number of tricks, determines
     * the number of bags the player got.
     ****/
    public int calculateBags(int bid, int actual)
    {
        // The zero case.
        if(bid == 0)
        {
            return 0;
        }
        else
        {
            if(actual > bid)
            {
                return (actual - bid);
            }
            else
            {
                return 0;
            }
        }
    }
}

