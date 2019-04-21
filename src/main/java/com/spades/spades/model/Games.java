package com.spades.spades.model;


import javax.persistence.*;

@Entity
@Table(name = "games", schema = "public")
public class Games {
    @Id
    @Column(name = "game_id")
    private Integer gameId;
    @Column(name = "player1_id")
    private Integer player1Id;
    @Column(name = "player2_id")
    private Integer player2Id;
    @Column(name = "game_status")
    private String gameStatus;
    @Column(name = "points_to_win")
    private int pointsToWin;
    @Column(name = "winner_id")
    private Integer winnerId;



    public Games(){}

    public Games(Games games) {
        this.player1Id = games.getPlayer1Id();
        this.player2Id = games.getPlayer2Id();
        this.gameStatus = games.getGameStatus();
        this.winnerId = games.getWinnerId();
        this.pointsToWin = games.getPointsToWin();
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(Integer gameId) {
        this.gameId = gameId;
    }

    public Integer getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(Integer player1Id) {
        this.player1Id = player1Id;
    }

    public Integer getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(Integer player2Id) {
        this.player2Id = player2Id;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public int getPointsToWin()
    {
        return pointsToWin;
    }

    public void setPointsToWin(int pointsToWin)
    {
        this.pointsToWin = pointsToWin;
    }

    public Integer getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Integer winnerId) {
        this.winnerId = winnerId;
    }
}
