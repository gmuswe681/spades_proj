package com.spades.spades.model;

import javax.persistence.*;
import java.util.Set;

public class Moves {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "move_number")
    private int moveNo;
    @Column(name= "round_number")
    private int roundNo;
    @Column(name="game_id")
    private int gameId;


  //  @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
//    @JoinTable(name = "rounds", joinColumns = @JoinColumn(name = "round_number"))
  //  private Set<Rounds> rounds;

  //  @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
 //   @JoinTable(name = "games", joinColumns = @JoinColumn(name = "game_id"))
    private Set<Games> games;

    public Moves(){}

    public Moves(Moves moves) {
        this.roundNo = moves.getRoundNo();
        this.gameId = moves.getGameId();
        this.rounds = moves.getRounds();
        this.games = moves.getGames();
    }

    public int getMoveNo() {
        return moveNo;
    }

    public void setMoveNo(int moveNo) {
        this.moveNo = moveNo;
    }

    public int getRoundNo() {
        return roundNo;
    }

    public void setRoundNo(int roundNo) {
        this.roundNo = roundNo;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public Set<Rounds> getRounds() {
        return rounds;
    }

    public void setRounds(Set<Rounds> rounds) {
        this.rounds = rounds;
    }

    public Set<Games> getGames() {
        return games;
    }

    public void setGames(Set<Games> games) {
        this.games = games;
    }
}
