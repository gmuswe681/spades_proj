package com.spades.spades;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Stack;

public class Deck {

    //Club= C; Diamonds = D; Hearts = H; Spades = S;
    private final static String[] SUITS = {
            "C", "D", "H", "S"
    };

    // Jack = J; Queen = Q; King = K; Ace = A;
    private final static String[] RANKS = {
            "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "J", "Q", "K", "A"
    };

    private Stack<String> deck = new Stack<String>();

    public Deck() {
        String card;
        for (int i = 0; i < RANKS.length; i++) {
            for (int j = 0; j < SUITS.length; j++) {
                card = RANKS[i] + SUITS[j];
                deck.push(card);
            }
        }

    }

    public void shuffle(){
        Collections.shuffle(deck, new SecureRandom());
    }

    public String drawCard(){
        return deck.peek();
    }

    public void keepOrReturnCard(){
        deck.pop();
    }

    public Integer getDeckLength(){
        return deck.size();
    }

}
