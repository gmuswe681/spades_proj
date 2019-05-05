package com.spades.spades.service;

import com.spades.spades.model.Games;
import com.spades.spades.repository.GamesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class GenerateGameIdService {
    
    @Autowired
    private GamesRepository gamesRepository;

    public int getNewGameId() {
        Sort idSort = new Sort(Sort.Direction.DESC, "gameId");

        List<Games> g = gamesRepository.findAll(idSort);
        if(g.size() > 0)
        {
            return g.get(0).getGameId() + 1;
        }

        return 1;
    }
}

