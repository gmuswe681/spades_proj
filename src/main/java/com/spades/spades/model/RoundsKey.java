package com.spades.spades.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class RoundsKey implements Serializable
{
    private static final long serialVersionUID = -2657326397810440808L;
    private int roundNumber;
    private int gameId;

    public RoundsKey() {

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

    public int hashCode() {
        return (new Integer(gameId)).hashCode() + (new Integer(roundNumber)).hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this)
        {
            return true;
        }
        if (!(obj instanceof RoundsKey)) 
        {
            return false;
        }

        RoundsKey pk = (RoundsKey) obj;
        return (pk.roundNumber == roundNumber) && (pk.gameId == gameId);
    }

}