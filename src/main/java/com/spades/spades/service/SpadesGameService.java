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

    public String progressGame(int gameId)
    {
        Optional<Games> foundGame = gamesRepository.findByGameId(gameId);
        if(foundGame.isPresent())
        {
            int playerId = currentPlayerInfoService.findPlayerId();
            if(playerId >= 0)
            {
                String round = createOrGetRound(gameId, playerId);
                if(round == "")
                {
                    return "Game: something went wrong";
                }
                return round;
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

    private String createOrGetRound(int gameId, int playerId)
    {
        Optional<Games> foundGame = gamesRepository.findByGameId(gameId);
        if(foundGame.isPresent())
        {
            Games g = foundGame.get();
            List<Rounds> roundsList = roundsRepository.findByGameId(gameId);

            int roundnum = 0;
            if(roundsList.size() == 0)
            {
                Rounds newRound = new Rounds();

                newRound.setGameId(g.getGameId());
                newRound.setRoundNumber(1);
                newRound.setPlayer1Id(g.getPlayer1Id());
                newRound.setPlayer2Id(g.getPlayer2Id());
                newRound.setRoundStatus("b");
                roundsRepository.save(newRound);

                spadeGamesStorage.put(new Integer(gameId), new SpadesRoundImpl());
                roundnum = 1;
            }
            else
            {
                roundnum = roundsList.get(0).getRoundNumber();
            }

            String result = "<h1>Round # " + roundnum + "</h1>\n";

            if(!spadeGamesStorage.containsKey(gameId))
            {
                //TODO: reconstruct a working game state?
                spadeGamesStorage.put(new Integer(gameId), new SpadesRoundImpl());
            }
            SpadesRoundImpl sGame = spadeGamesStorage.get(gameId);

            result += "<p>Your hand:</p>\n";
            if(playerId == g.getPlayer1Id())
            {
                result += "<p>";
                Hand p1Hand = sGame.getHand1();
                for(String s : p1Hand.getHand())
                {
                    result += s + " ";
                }
                result += "</p>\n";
            }
            else if(playerId == g.getPlayer2Id())
            {
                result += "<p>";
                Hand p2Hand = sGame.getHand2();
                for(String s : p2Hand.getHand())
                {
                    result += s + " ";
                }
                result += "</p>\n";
            }
            else
            {
                return "Invalid";
            }

            return result;
        }

        return "Invalid";
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

    public void submitBid(int gameId, int amount)
    {
        Rounds r = getCurrentRoundStatus(gameId);
        int playerId = currentPlayerInfoService.findPlayerId();

        if(r == null || amount < 0 || amount > 13)
        {
            return;
        }

        if(r.getPlayer1Id() == playerId)
        {
            r.setPlayer1Bid(amount);
            roundsRepository.save(r);
        }
        else if(r.getPlayer2Id() == playerId)
        {
            r.setPlayer2Bid(amount);
            roundsRepository.save(r);
        }
    }
}

