package com.spades.spades.repository;
import com.spades.spades.model.Moves;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import javax.transaction.Transactional;

public interface MovesRepository extends JpaRepository<Moves, Integer>{
    List<Moves> findByGameIdOrderByMoveIdAsc(int gameId);

    @Transactional
    long deleteByGameIdAndRoundId(int gameId, int roundId);
}
