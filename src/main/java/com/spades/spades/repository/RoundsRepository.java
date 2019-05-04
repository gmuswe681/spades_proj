package com.spades.spades.repository;

import java.util.List;
import java.util.Optional;

import com.spades.spades.model.Rounds;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundsRepository extends JpaRepository<Rounds, Integer> {
    List<Rounds> findByGameId(int gameId);
    List<Rounds> findByGameIdOrderByRoundNumberAsc(int gameId);

    Optional<Rounds> findByGameIdAndRoundStatusNot(int gameId, String roundStatus);
    Optional<Rounds> findByGameIdAndRoundNumber(int gameId, int roundNumber);
}