package com.spades.spades.repository;

import com.spades.spades.model.Games;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GamesRepository extends JpaRepository<Games, Integer> {

}