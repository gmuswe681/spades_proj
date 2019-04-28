package com.spades.spades.model;

import javax.persistence.*;
import java.util.Set;

public class Moves {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "move_id")
    private int moveId;
    @Column(name= "round_id")
    private int roundId;
    @Column(name="game_id")
    private int gameId;
    @Column(name="user_id")
    private int userId;
    @Column(name="card_played")
    private String cardPlayed;

  //  @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
//    @JoinTable(name = "rounds", joinColumns = @JoinColumn(name = "round_number"))
  //  private Set<Rounds> rounds;

  //  @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
 //   @JoinTable(name = "games", joinColumns = @JoinColumn(name = "game_id"))
  //  private Set<Games> games;

    public Moves(){}

    public Moves(Moves moves) {
        this.moveId = moves.getMoveNo();
        this.gameId = moves.getGameId();
        this.roundId = moves.getRoundNo();
        this.userId = moves.getUserId();
    //    this.rounds = moves.getRounds();
    //    this.games = moves.getGames();
    }

    public int getMoveNo() {
        return this.moveId;
    }

    public void setMoveNo(int moveNo) {
        this.moveId = moveNo;
    }

    public int getRoundNo() {
        return this.roundId;
    }

    public void setRoundNo(int roundNo) {
        this.roundId = roundNo;
    }

    public int getGameId() {return this.gameId; }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getUserId() {return this.userId;}

    public void setUserId(int userId){this.userId = userId;}

//    public Set<Rounds> getRounds() {
//        return rounds;
//    }

//    public void setRounds(Set<Rounds> rounds) {
//        this.rounds = rounds;
//    }

//    public Set<Games> getGames() {
//        return games;
//    }
//
//    public void setGames(Set<Games> games) {
//        this.games = games;
//    }
}
