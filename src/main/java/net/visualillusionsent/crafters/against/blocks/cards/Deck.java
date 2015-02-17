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

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Copyright (C) 2015 Visual Illusions Entertainment
 * All Rights Reserved.
 *
 * @author Jason Jones (darkdiplomat)
 */
public abstract class Deck<E extends Card> {
    protected final ArrayList<Card> cards = Lists.newArrayList();
    protected int used = 0;

    public final void shuffle() {
        used = 0;
        Collections.shuffle(cards);
    }

    public final boolean removeCard(E card) {
        return cards.remove(card);
    }

    public final void addCard(E card) {
        cards.add(card);
    }

    public abstract E dealCard();
}