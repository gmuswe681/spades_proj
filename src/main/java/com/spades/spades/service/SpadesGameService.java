package com.spades.spades.service;

import com.spades.spades.model.Games;
import com.spades.spades.repository.GamesRepository;

import com.spades.spades.model.Rounds;
import com.spades.spades.repository.RoundsRepository;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class SpadesGameService {

    @Autowired
    private GamesRepository gamesRepository;

    @Autowired
    private RoundsRepository roundsRepository;

    public String progressGame(int gameId)
    {
        Optional<Games> foundGame = gamesRepository.findByGameId(gameId);
        if(foundGame.isPresent())
        {
            int roundNum = createOrGetRound(gameId);
            return "Round #" + roundNum;
        }
        else
        {
            return "Game not found";
        }
    }

    private int createOrGetRound(int gameId)
    {
        Optional<Games> foundGame = gamesRepository.findByGameId(gameId);
        if(foundGame.isPresent())
        {
            Games g = foundGame.get();
            List<Rounds> roundsList = roundsRepository.findByGameId(gameId);

            if(roundsList.size() == 0)
            {
                Rounds newRound = new Rounds();

                newRound.setGameId(g.getGameId());
                newRound.setRoundNumber(1);
                newRound.setPlayer1Id(g.getPlayer1Id());
                newRound.setPlayer2Id(g.getPlayer2Id());
                roundsRepository.save(newRound);

                return newRound.getRoundNumber();
            }
            else
            {
                Rounds currRound = roundsList.get(0);
                return currRound.getRoundNumber();
            }
        }

        return 0;
    }
}

