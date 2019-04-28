package com.spades.spades.repository;
import com.spades.spades.model.Moves;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovesRepository extends JpaRepository<Moves, Integer>{
    List<Moves> findByGameId(int gameId);
}
