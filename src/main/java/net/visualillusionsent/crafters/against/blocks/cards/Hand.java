/*
 * This file is part of CraftersAgainstBlocks.
 *
 * Copyright © 2015 Visual Illusions Entertainment
 *
 * CraftersAgainstBlocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License v3 as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License v3 for more details.
 *
 * You should have received a copy of the GNU General Public License v3 along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.crafters.against.blocks.cards;

import net.visualillusionsent.utils.Verify;

import java.util.Arrays;
import java.util.Vector;

/**
 * Copyright (C) 2015 Visual Illusions Entertainment
 * All Rights Reserved.
 *
 * @author Jason Jones (darkdiplomat)
 */
public final class Hand {
    private Vector<WhiteCard> hand; // The cards in the hand.

    public Hand() {
        // Create a Hand object that is initially empty.
        hand = new Vector<WhiteCard>(10);
    }

    public void clear() {
        // Discard all the cards from the hand.
        hand.removeAllElements();
    }

    public void addCard(WhiteCard card) {
        /*
        Add the card to the hand. card should be non-null.
        (If card is null, nothing is added to the hand.)
        */
        Verify.notNull(card, "Card card");
        hand.addElement(card);
    }

    public void removeCard(WhiteCard card) {
        // If the specified card is in the hand, it is removed.
        hand.removeElement(card);
    }

    public WhiteCard removeCard(int position) {
        /*
        If the specified position is a valid position in the hand,
        then the card in that position is removed.
        */
        if (position >= 0 && position < hand.size()) {
            WhiteCard toRet = hand.get(position);
            hand.removeElementAt(position);
            return toRet;
        }
        return null;
    }

    public int getCardCount() {
        // Return the number of cards in the hand.
        return hand.size();
    }

    public WhiteCard getCard(int position) {
        /*
        Get the card from the hand in given position, where positions
        are numbered starting from 0. If the specified position is
        not the position number of a card in the hand, then null
        is returned.
        */
        if (position >= 0 && position < hand.size()) {
            return hand.elementAt(position);
        }
        return null;
    }

    public String toString() {
        return Arrays.toString(hand.toArray(new Card[10]));
    }
}
