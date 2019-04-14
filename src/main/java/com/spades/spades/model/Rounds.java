package com.spades.spades.model;

import javax.persistence.*;
import java.util.Set;

public class Rounds {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "round_number")
    private int roundNumber;
    @Column(name = "game_id")
    private int gameId;
    @Column(name = "player1_id")
    private int player1Id;
    @Column(name = "player2_id")
    private int player2Id;
    @Column(name = "player1_bid")
    private int player1Bid;
    @Column(name = "player2_bid")
    private int player2Bid;
    @Column(name = "player1_actual")
    private int player1Actual;
    @Column(name = "player2_actual")
    private int player2Actual;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "games", joinColumns = @JoinColumn(name = "game_id"))
    private Set<Games> games;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(name = "user", joinColumns = @JoinColumn(name = "user_id"))
    private Set<Users> users;

    public Rounds(){}

    public Rounds(Rounds rounds) {
        this.gameId = rounds.getGameId();
        this.player1Id = rounds.getPlayer1Id();
        this.player2Id = rounds.getPlayer2Id();
        this.player1Bid = rounds.getPlayer1Bid();
        this.player2Bid = rounds.getPlayer2Bid();
        this.player1Actual = rounds.getPlayer1Actual();
        this.player2Actual = rounds.getPlayer2Actual();
        this.games = rounds.getGames();
        this.users = rounds.getUsers();
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(int player1Id) {
        this.player1Id = player1Id;
    }

    public int getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(int player2Id) {
        this.player2Id = player2Id;
    }

    public int getPlayer1Bid() {
        return player1Bid;
    }

    public void setPlayer1Bid(int player1Bid) {
        this.player1Bid = player1Bid;
    }

    public int getPlayer2Bid() {
        return player2Bid;
    }

    public void setPlayer2Bid(int player2Bid) {
        this.player2Bid = player2Bid;
    }

    public int getPlayer1Actual() {
        return player1Actual;
    }

    public void setPlayer1Actual(int player1Actual) {
        this.player1Actual = player1Actual;
    }

    public int getPlayer2Actual() {
        return player2Actual;
    }

    public void setPlayer2Actual(int player2Actual) {
        this.player2Actual = player2Actual;
    }

    public Set<Games> getGames() {
        return games;
    }

    public void setGames(Set<Games> games) {
        this.games = games;
    }

    public Set<Users> getUsers() {
        return users;
    }

    public void setUsers(Set<Users> users) {
        this.users = users;
    }
}
