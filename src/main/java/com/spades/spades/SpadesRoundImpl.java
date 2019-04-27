/****
 * Represents the current state of the Spades round in progress.
 * I.E. maintains the hands, current player's turn, etc.
 ****/

package com.spades.spades;

public class SpadesRoundImpl {

    private Deck deck;

    private Hand hand1;
    private Hand hand2;

    public SpadesRoundImpl() {
        this.deck = new Deck();
        this.deck.shuffle();

        hand1 = new Hand();
        hand2 = new Hand();

        for(int i = 0; i < 13; i++)
        {
            hand1.addToHand(deck.drawCard());
            deck.keepOrReturnCard();
            hand2.addToHand(deck.drawCard());
            deck.keepOrReturnCard();
        }
    }

    public Hand getHand1()
    {
        return hand1;
    }

    public Hand getHand2()
    {
        return hand2;
    }

    public Deck getDeck()
    {
        return deck;
    }
}
