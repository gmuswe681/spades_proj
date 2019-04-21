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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger LOGGER = LogManager.getLogger("SpadesGameService.class");

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
                roundsRepository.save(newRound);

                spadeGamesStorage.put(new Integer(gameId), new SpadesRoundImpl());
                roundnum = 1;
            }
            else
            {
                roundnum = roundsList.get(0).getRoundNumber();
            }

            String result = "Round # " + roundnum + "\n";

            if(!spadeGamesStorage.containsKey(gameId))
            {
                spadeGamesStorage.put(new Integer(gameId), new SpadesRoundImpl());
            }
            SpadesRoundImpl sGame = spadeGamesStorage.get(gameId);

            result += "Your hand:\n";
            if(playerId == g.getPlayer1Id())
            {
                Hand p1Hand = sGame.getHand1();
                for(String s : p1Hand.getHand())
                {
                    result += s + "\n";
                }
            }
            else if(playerId == g.getPlayer2Id())
            {
                Hand p2Hand = sGame.getHand2();
                for(String s : p2Hand.getHand())
                {
                    result += s + "\n";
                }
            }
            else
            {
                return "Invalid";
            }

            LOGGER.debug("Deck size: " + sGame.getDeck().getDeckLength());
            return result;
        }

        return "Invalid";
    }
}

