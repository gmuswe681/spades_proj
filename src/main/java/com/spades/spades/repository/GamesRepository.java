package com.spades.spades.repository;

import java.util.List;
import java.util.Optional;

import com.spades.spades.model.Games;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GamesRepository extends JpaRepository<Games, Integer> {
    List<Games> findByGameStatus(String status);
    Optional<Games> findByGameId(int gameId);

    @Query("select g from Games g WHERE (g.player1Id = ?1 OR g.player2Id = ?1) AND not(g.gameStatus = 'e')")
    List<Games> findOpenGamesForUser(int userId);
}