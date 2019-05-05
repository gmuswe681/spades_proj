package com.spades.spades;



import java.util.ArrayList;

public final class Hand {

    private  ArrayList<String> hand;
    private static final Integer maxHandSize = 13;

    public Hand(){
        hand  = new ArrayList<String>();
    }


    public ArrayList<String> getHand(){
        return hand;
    }

    public void addToHand(String card){
        if(hand.size() < maxHandSize)
        hand.add(card);
    }

    public void removeFromHand(String card){
        hand.remove(card);
    }

    public boolean checkInHand(String card){
        if (card == null)
        {
            return false;
        }

        return hand.contains(card);
    }

    public Integer returnHandSize(){
        return hand.size();
    }
}
