package com.spades.spades.model;

import javax.persistence.*;

@Entity
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

    public Moves(){}

    public Moves(Moves moves) {
        this.moveId = moves.getMoveNo();
        this.gameId = moves.getGameId();
        this.roundId = moves.getRoundNo();
        this.userId = moves.getUserId();
        this.cardPlayed = moves.getCardPlayed();
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

    public void setCardPlayed(String cardPlayed) {this.cardPlayed = cardPlayed;}

    public String getCardPlayed(){return this.cardPlayed;}

}
