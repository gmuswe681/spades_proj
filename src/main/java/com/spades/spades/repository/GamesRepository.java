package com.spades.spades.repository;

import java.util.List;
import java.util.Optional;

import com.spades.spades.model.Games;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GamesRepository extends JpaRepository<Games, Integer> {
    List<Games> findByGameStatus(String status);
    Optional<Games> findByGameId(int gameId);
}