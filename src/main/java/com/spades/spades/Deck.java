package com.spades.spades;

import java.util.Collections;
import java.util.Stack;

public class Deck {

    private final static String[] SUITS = {
            "Clubs", "Diamonds", "Hearts", "Spades"
    };

    private final static String[] RANKS = {
            "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "Jack", "Queen", "King", "Ace"
    };

    private Stack<String> deck = new Stack<String>();

    private final static int n = SUITS.length * RANKS.length;

    public Deck() {
        String card;
        for (int i = 0; i < RANKS.length; i++) {
            for (int j = 0; j < SUITS.length; j++) {
                card = RANKS[i] + " of " + SUITS[j];
                deck.push(card);
            }
        }

    }

    public void shuffle(){
        Collections.shuffle(deck);
    }

    public String drawCard(){
        return deck.peek();
    }

    public void keepOrReturnCard(){
        deck.pop();
    }

}
