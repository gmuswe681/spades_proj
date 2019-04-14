package com.spades.spades;

public class Deck {

    private final static String[] SUITS = {
            "Clubs", "Diamonds", "Hearts", "Spades"
    };

    private final static String[] RANKS = {
            "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "Jack", "Queen", "King", "Ace"
    };

    private final static int n = SUITS.length * RANKS.length;

    public Deck() {
        String[] deck = new String[n];
        for (int i = 0; i < RANKS.length; i++) {
            for (int j = 0; j < SUITS.length; j++) {
                deck[SUITS.length * i + j] = RANKS[i] + " of " + SUITS[j];
            }
        }

    }

    public void Shuffle(String[] deck){
        // shuffle
        for (int i = 0; i < n; i++) {
            int r = i + (int) (Math.random() * (n - i));
            String temp = deck[r];
            deck[r] = deck[i];
            deck[i] = temp;
        }
    }

}
