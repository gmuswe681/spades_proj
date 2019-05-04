/****
 * Represents the current state of the Spades round in progress.
 * I.E. maintains the hands, current player's turn, etc.
 ****/

package com.spades.spades;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class SpadesRoundImpl {

    private Deck deck;

    private Hand hand1;
    private Hand hand2;

    // Represents if it is player 1's turn or player 2's turn
    private int currentTurn;

    // For the current 'trick', indicates which player initiated the play.
    private int firstPlayer;

    // For this round, indicates whether spades has been broken.
    private boolean spadesBroken;

    // Represents cards in the current trick.
    private String player1Card;
    private String player2Card;

    // A timeout for a given game.
    private HandTimeOut timer;

    public SpadesRoundImpl() {
        this.deck = new Deck();
        this.deck.shuffle();

        hand1 = new Hand();
        hand2 = new Hand();

        // Deals out the cards.
        for(int i = 0; i < 13; i++)
        {
            hand1.addToHand(deck.drawCard());
            deck.keepOrReturnCard();
            hand2.addToHand(deck.drawCard());
            deck.keepOrReturnCard();
        }

        // Randomizes who gets to play the first card.
        currentTurn = (new SecureRandom()).nextInt(2) + 1;

        // Sets up initial game conditions
        firstPlayer = currentTurn;
        spadesBroken = false;
        player1Card = "";
        player2Card = "";

        timer = new HandTimeOut(180000L);
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

    public void setCurrentTurn(int currentTurn)
    {
        this.currentTurn = currentTurn;
    }

    public int getCurrentTurn()
    {
        return currentTurn;
    }

    public String getPlayer1Card()
    {
        return player1Card;
    }

    public String getPlayer2Card()
    {
        return player2Card;
    }

    public boolean gameTimedOut()
    {
        return timer.getTimedOut();
    }

    /****
     * Attempts to play a card from player 1's hand
     * returns true if the move was successful.
     ****/
    public boolean playHand1(String card)
    {
        if(timer.getTimedOut())
        {
            return false;
        }

        // Checks that it is player 1's turn.
        if(currentTurn != 1)
        {
            return false;
        }

        // Checks that player 1 has the card.
        if(!hand1.checkInHand(card))
        {
            return false;
        }

        // Checks that player 1 hasn't already played a card.
        if(!player1Card.equals(""))
        {
            return false;
        }

        // Checks if player is moving first, then performs corresponding checks
        if(firstPlayer == 1)
        {
            // If player is playing a Spades Suit, and spades hasn't been broken yet, then
            // check that the user only has Spades left.
            if(card.contains("S") && !spadesBroken)
            {
                for(String handCard : hand1.getHand())
                {
                    if(!handCard.contains("S"))
                    {
                        return false;
                    }
                }
            }

            // Plays the card
            player1Card = card;
            hand1.removeFromHand(card);

            // Updates turn counters
            currentTurn = 2;
            return true;
        }
        else
        {
            // Checks the leading suit.
            String leadCard = player2Card;
            String suit = extractSuit(leadCard);
            if(suit == null)
            {
                return false;
            }

            // If the played card doesn't match the current suit, then check the user's hand
            // to ensure that the user doesn't have any matching cards.
            if(!card.contains(suit))
            {
                for(String handCard : hand1.getHand())
                {
                    if(handCard.contains(suit))
                    {
                        return false;
                    }
                }
            }

            // Plays the card
            player1Card = card;
            hand1.removeFromHand(card);

            // Updates if Spades has been broken
            if(card.contains("S"))
            {
                spadesBroken = true;
            }

            // Sets current turn to 0, to ensure played cards are checked first.
            currentTurn = 0;
            resetTimer();
            return true;
        }
    }

    /****
     * Attempts to play a card from player 2's hand
     * returns true if the move was successful.
     ****/
    public boolean playHand2(String card)
    {
        if(timer.getTimedOut())
        {
            return false;
        }

        // Checks that it is player 2's turn.
        if(currentTurn != 2)
        {
            return false;
        }

        // Checks that player 2 has the card.
        if(!hand2.checkInHand(card))
        {
            return false;
        }

        // Checks that player 2 hasn't already played a card.
        if(!player2Card.equals(""))
        {
            return false;
        }

        // Checks if player is moving first, then performs corresponding checks
        if(firstPlayer == 2)
        {
            // If player is playing a Spades Suit, and spades hasn't been broken yet, then
            // check that the user only has Spades left.
            if(card.contains("S") && !spadesBroken)
            {
                for(String handCard : hand2.getHand())
                {
                    if(!handCard.contains("S"))
                    {
                        return false;
                    }
                }
            }

            // Plays the card
            player2Card = card;
            hand2.removeFromHand(card);

            // Updates turn counters
            currentTurn = 1;
            return true;
        }
        else
        {
            // Checks the leading suit.
            String leadCard = player1Card;
            String suit = extractSuit(leadCard);
            if(suit == null)
            {
                return false;
            }

            // If the played card doesn't match the current suit, then check the user's hand
            // to ensure that the user doesn't have any matching cards.
            if(!card.contains(suit))
            {
                for(String handCard : hand2.getHand())
                {
                    if(handCard.contains(suit))
                    {
                        return false;
                    }
                }
            }

            // Plays the card
            player2Card = card;
            hand2.removeFromHand(card);

            // Updates if Spades has been broken
            if(card.contains("S"))
            {
                spadesBroken = true;
            }

            // Sets current turn to 0, to ensure played cards are checked first.
            currentTurn = 0;
            resetTimer();
            return true;
        }
    }

    /****
     * Provided that all players have played a card, check which player won the trick.
     * If conditions are met, will clear out played cards and update the current turn.
     * Returns the player #(1 | 2) that won, or -1 if conditions aren't met
     * NOTE: J = 11, Q = 12, K = 13, A = 14
     ****/
    public int calculateTrick()
    {
        // Checks that cards have been played.
        if(player1Card.equals("") || player2Card.equals(""))
        {
            return -1;
        }

        // Extracts the lead suit and lead value from the game.
        String leadSuit = "";
        int leadValue = -1;
        String followSuit = "";
        int followValue = -1;
        if(firstPlayer == 1)
        {
            leadSuit = extractSuit(player1Card);
            leadValue = extractValue(player1Card);
            followSuit = extractSuit(player2Card);
            followValue = extractValue(player2Card);
        }
        else if(firstPlayer == 2)
        {
            leadSuit = extractSuit(player2Card);
            leadValue = extractValue(player2Card);
            followSuit = extractSuit(player1Card);
            followValue = extractValue(player1Card);
        }
        else
        {
            return -1;
        }

        // Checks whether the following player beat the lead player.
        int winner = -1;

        // Checks for matching suit.
        if(followSuit.equals(leadSuit))
        {
            // Checks card values.
            if(followValue > leadValue)
            {
                if (firstPlayer == 1)
                {
                    winner = 2;
                }
                else
                {
                    winner = 1;
                }
            }
            else
            {
                winner = firstPlayer;
            }
        }
        else
        {
            // Not matching suit, so first player wins unless player played a Spades
            if(followSuit.equals("S"))
            {
                if (firstPlayer == 1)
                {
                    winner = 2;
                }
                else
                {
                    winner = 1;
                }
            }
            else
            {
                winner = firstPlayer;
            }
        }

        // Resets for the next trick.
        firstPlayer = winner;
        currentTurn = firstPlayer;
        player1Card = "";
        player2Card = "";
        
        return winner;
    }

    /****
     * Returns the corresponding Value represented by this card (2, 3, 4, 5, 6, 7, 8, 9, 10, J, Q, K, A)
     * or -1 if not found.
     * NOTE: J = 11, Q = 12, K = 13, A = 14
     ****/
    private int extractValue(String card)
    {
        // private Map for card values to a numerical value
        HashMap<String, Integer> cardVals = new HashMap<String, Integer>();
        cardVals.put("2", 2);
        cardVals.put("3", 3);
        cardVals.put("4", 4);
        cardVals.put("5", 5);
        cardVals.put("6", 6);
        cardVals.put("7", 7);
        cardVals.put("8", 8);
        cardVals.put("9", 9);
        cardVals.put("10", 10);
        cardVals.put("J", 11);
        cardVals.put("Q", 12);
        cardVals.put("K", 13);
        cardVals.put("A", 14);

        Iterator<Entry<String, Integer>> iter = cardVals.entrySet().iterator();
        while(iter.hasNext())
        {
            Entry<String, Integer> val = iter.next();
            if(card.contains(val.getKey()))
            {
                return val.getValue().intValue();
            }
        }

        return -1;
    }

    /****
     * Returns the corresponding suit represented by this card (S, D, C, H),
     * or null if not found
     ****/
    private String extractSuit(String card)
    {
        if(card.contains("S"))
        {
            return "S";
        }
        else if(card.contains("D"))
        {
            return "D";
        }
        else if(card.contains("C"))
        {
            return "C";
        }
        else if(card.contains("H"))
        {
            return "H";
        }

        return null;
    }

    /****
     * Resets the internal timer
     ****/
    private void resetTimer()
    {
        if((timer == null) || !(timer.getTimedOut()))
        {
            if(timer != null)   
            {
                timer.cancelTimeout();
            }
            timer = new HandTimeOut(180000L);
    
        }
    }
}
