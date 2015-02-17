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
package net.visualillusionsent.crafters.against.blocks.user;

import net.visualillusionsent.crafters.against.blocks.cards.Hand;
import net.visualillusionsent.crafters.against.blocks.cards.WhiteCard;

import java.util.UUID;

/**
 * Copyright (C) 2015 Visual Illusions Entertainment
 * All Rights Reserved.
 *
 * @author Jason Jones (darkdiplomat)
 */
public final class User {
    private final Hand hand = new Hand();
    private final UUID uuid;

    public User(UUID uuid) {
        this.uuid = uuid;

        for (int count = 0; count < 10; count++) {
            //hand.addCard(CraftersAgainstBlocks.dealWhiteCard());
        }
    }

    public final UUID getUserUUID() {
        return uuid;
    }

    public WhiteCard playCard(int position) {
        return hand.removeCard(position);
    }

    public final boolean equals(Object obj) {
        return !(!(obj instanceof User) && !(obj instanceof UUID)) && (uuid.equals(obj) || super.equals(obj));
    }
}
