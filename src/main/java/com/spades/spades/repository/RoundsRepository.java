package com.spades.spades.repository;

import java.util.List;

import com.spades.spades.model.Rounds;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoundsRepository extends JpaRepository<Rounds, Integer> {
    List<Rounds> findByGameId(int gameId);
}